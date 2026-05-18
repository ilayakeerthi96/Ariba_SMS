import {
  Component, OnInit, OnDestroy, AfterViewChecked,
  ElementRef, ViewChild
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { DataService } from '../../../shared/service/DataService';
import { ChatWebSocketService, ChatMessage } from '../../../shared/service/ChatWebSocketService';
import { AuthService } from '../../../shared/service/AuthService';

interface ChatRoom {
  roomId: number;
  rfqId: number;
  rfqNumber: string;
  rfqTitle: string;
  buyerId: number;
  supplierId: number;
  buyerName: string;
  supplierName: string;
}

interface DisplayMessage {
  id?: number;
  senderType: string;
  senderName: string;
  message: string;
  sentAt: string;
  isMine: boolean;
}

@Component({
  selector: 'app-rfq-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './rfq-chat.component.html',
  styleUrls: ['./rfq-chat.component.scss']
})
export class RfqChatComponent implements OnInit, OnDestroy, AfterViewChecked {

  @ViewChild('messagesEnd') private messagesEnd!: ElementRef;

  rfqId!: number;
  roomId!: number;
  chatRoom: ChatRoom | null = null;
  messages: DisplayMessage[] = [];
  newMessage = '';

  isLoading = true;
  isSending = false;
  wsConnected = false;
  error = '';

  currentUserId!: number;
  currentSenderType!: 'BUYER' | 'SUPPLIER';
  currentSenderName!: string;
  peerName = '';

  private wsSub: Subscription | null = null;
  private shouldScroll = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private dataService: DataService,
    private chatWs: ChatWebSocketService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.rfqId = Number(this.route.snapshot.paramMap.get('rfqId'));
    this.resolveCurrentUser();
    this.loadChatRoom();
  }

  // ─── INITIALISATION ───────────────────────────────────────────────────────

  private resolveCurrentUser(): void {
    const user = (this.authService as any).currentUser || {};

    if (this.authService.isSupplier()) {
      this.currentSenderType = 'SUPPLIER';
      this.currentUserId = user.supplierId ?? user.id ?? 0;
      this.currentSenderName = user.companyName ?? user.name ?? 'Supplier';
    } else {
      this.currentSenderType = 'BUYER';
      this.currentUserId = user.buyerId ?? user.id ?? 0;
      this.currentSenderName = user.companyName ?? user.name ?? 'Buyer';
    }
  }

  private loadChatRoom(): void {
    this.dataService.getChatRoomByRfq(this.rfqId).subscribe({
      next: (res: any) => {
        if (res?.success && res?.data) {
          this.chatRoom = res.data;
          this.roomId = res.data.roomId;
          this.peerName = this.currentSenderType === 'BUYER'
            ? res.data.supplierName
            : res.data.buyerName;
          this.loadMessages();
          this.connectWebSocket();
        } else {
          this.error = 'Chat is not available yet. The supplier has not been selected for this RFQ.';
          this.isLoading = false;
        }
      },
      error: () => {
        this.error = 'Failed to load chat room. Please try again.';
        this.isLoading = false;
      }
    });
  }

  private loadMessages(): void {
    this.dataService.getChatMessages(this.roomId, this.currentSenderType).subscribe({
      next: (res: any) => {
        if (res?.success) {
          this.messages = (res.data || []).map((m: any) => this.toDisplay(m));
          this.shouldScroll = true;
        }
        this.isLoading = false;
      },
      error: () => { this.isLoading = false; }
    });
  }

  private async connectWebSocket(): Promise<void> {
    try {
      await this.chatWs.connect();
      this.wsConnected = true;
      this.wsSub = this.chatWs.subscribeToRoom(this.roomId).subscribe(msg => {
        this.messages.push(this.toDisplay(msg));
        this.shouldScroll = true;
        // Auto mark as read when the other party sends a message
        if (msg.senderType !== this.currentSenderType) {
          this.dataService.markChatAsRead(this.roomId, this.currentSenderType).subscribe();
        }
      });
    } catch {
      this.wsConnected = false;
    }
  }

  // ─── SEND ─────────────────────────────────────────────────────────────────

  sendMessage(): void {
    const text = this.newMessage.trim();
    if (!text || this.isSending) return;

    this.isSending = true;
    this.chatWs.sendMessage(
      this.roomId,
      this.currentUserId,
      this.currentSenderType,
      this.currentSenderName,
      text
    );
    this.newMessage = '';
    this.isSending = false;
  }

  onEnterKey(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }

  // ─── HELPERS ──────────────────────────────────────────────────────────────

  private toDisplay(m: any): DisplayMessage {
    return {
      id: m.id,
      senderType: m.senderType,
      senderName: m.senderName,
      message: m.message,
      sentAt: m.sentAt,
      isMine: m.senderType === this.currentSenderType
    };
  }

  formatTime(sentAt: string): string {
    if (!sentAt) return '';
    try {
      return new Date(sentAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch { return ''; }
  }

  formatDateLabel(sentAt: string): string {
    if (!sentAt) return '';
    try {
      const d = new Date(sentAt);
      const today = new Date();
      const yesterday = new Date(today);
      yesterday.setDate(today.getDate() - 1);
      if (d.toDateString() === today.toDateString()) return 'Today';
      if (d.toDateString() === yesterday.toDateString()) return 'Yesterday';
      return d.toLocaleDateString([], { day: 'numeric', month: 'short', year: 'numeric' });
    } catch { return ''; }
  }

  shouldShowDateLabel(index: number): boolean {
    if (index === 0) return true;
    const curr = this.messages[index].sentAt;
    const prev = this.messages[index - 1].sentAt;
    if (!curr || !prev) return false;
    return new Date(curr).toDateString() !== new Date(prev).toDateString();
  }

  goBack(): void {
    history.back();
  }

  // ─── LIFECYCLE ────────────────────────────────────────────────────────────

  ngAfterViewChecked(): void {
    if (this.shouldScroll) {
      this.scrollToBottom();
      this.shouldScroll = false;
    }
  }

  private scrollToBottom(): void {
    try {
      this.messagesEnd.nativeElement.scrollIntoView({ behavior: 'smooth' });
    } catch {}
  }

  ngOnDestroy(): void {
    this.wsSub?.unsubscribe();
    if (this.roomId) this.chatWs.unsubscribeFromRoom(this.roomId);
  }
}
