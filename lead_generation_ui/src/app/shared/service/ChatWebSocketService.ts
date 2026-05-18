import { Injectable, OnDestroy } from '@angular/core';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ChatMessage {
  id?: number;
  roomId: number;
  senderId: number;
  senderType: 'BUYER' | 'SUPPLIER';
  senderName: string;
  message: string;
  sentAt?: string;
  isRead?: boolean;
}

@Injectable({ providedIn: 'root' })
export class ChatWebSocketService implements OnDestroy {

  private client: Client | null = null;
  private messageSubjects = new Map<number, Subject<ChatMessage>>();
  private subscriptions = new Map<number, StompSubscription>();
  private connectionStatus = new BehaviorSubject<boolean>(false);

  isConnected$ = this.connectionStatus.asObservable();

  connect(): Promise<void> {
    return new Promise((resolve, reject) => {
      if (this.client?.connected) {
        resolve();
        return;
      }

      this.client = new Client({
        webSocketFactory: () => new SockJS(`${environment.WS_URL}/ws/chat`),
        reconnectDelay: 5000,
        onConnect: () => {
          this.connectionStatus.next(true);
          // Re-subscribe to any rooms that were subscribed before reconnect
          this.messageSubjects.forEach((_, roomId) => {
            this.doSubscribe(roomId);
          });
          resolve();
        },
        onDisconnect: () => {
          this.connectionStatus.next(false);
          this.subscriptions.clear();
        },
        onStompError: (frame) => {
          console.error('[ChatWS] STOMP error', frame);
          reject(new Error(frame.headers?.['message'] || 'STOMP error'));
        }
      });

      this.client.activate();
    });
  }

  disconnect(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.subscriptions.clear();
    this.client?.deactivate();
    this.connectionStatus.next(false);
  }

  subscribeToRoom(roomId: number): Observable<ChatMessage> {
    if (!this.messageSubjects.has(roomId)) {
      this.messageSubjects.set(roomId, new Subject<ChatMessage>());
    }
    if (this.client?.connected && !this.subscriptions.has(roomId)) {
      this.doSubscribe(roomId);
    }
    return this.messageSubjects.get(roomId)!.asObservable();
  }

  sendMessage(roomId: number, senderId: number, senderType: string,
              senderName: string, message: string): void {
    if (!this.client?.connected) {
      console.warn('[ChatWS] Not connected — message not sent');
      return;
    }
    this.client.publish({
      destination: `/app/chat.send/${roomId}`,
      body: JSON.stringify({ roomId, senderId, senderType, senderName, message })
    });
  }

  unsubscribeFromRoom(roomId: number): void {
    const sub = this.subscriptions.get(roomId);
    if (sub) {
      sub.unsubscribe();
      this.subscriptions.delete(roomId);
    }
    this.messageSubjects.delete(roomId);
  }

  private doSubscribe(roomId: number): void {
    if (!this.client?.connected) return;
    const sub = this.client.subscribe(`/topic/chat/${roomId}`, (msg: IMessage) => {
      try {
        const chatMsg: ChatMessage = JSON.parse(msg.body);
        this.messageSubjects.get(roomId)?.next(chatMsg);
      } catch (e) {
        console.error('[ChatWS] Failed to parse message', e);
      }
    });
    this.subscriptions.set(roomId, sub);
  }

  ngOnDestroy(): void {
    this.disconnect();
  }
}
