#!/bin/bash

# -----------------------------------
# CONFIGURATION
# -----------------------------------
APP_NAME="lead-generation-ui"
BUILD_DIR="dist/lead-generation-ui"   # Angular's output directory
DEPLOY_DIR="/opt/leadcapture/Frontend/prod"
BACKUP_DIR="/opt/leadcapture/Frontend/backup"
RUN_USER="uatserver"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
NGINX_SERVICE="nginx"

# -----------------------------------
# 1. BUILD THE ANGULAR APP
# -----------------------------------
echo "📦 Building Angular application..."
npm install --legacy-peer-deps
ng build --configuration production

if [ $? -ne 0 ]; then
  echo "❌ Angular build failed!"
  exit 1
fi
echo "✅ Angular build completed."

# -----------------------------------
# 2. VERIFY BUILD OUTPUT EXISTS
# -----------------------------------
if [ ! -d "$BUILD_DIR" ]; then
  echo "❌ Error: Build output folder '$BUILD_DIR' not found!"
  exit 1
fi

# -----------------------------------
# 3. BACKUP EXISTING DEPLOYED VERSION
# -----------------------------------
if [ -d "$DEPLOY_DIR" ]; then
  mkdir -p "$BACKUP_DIR"
  BACKUP_PATH="$BACKUP_DIR/${APP_NAME}_backup_$TIMESTAMP"
  sudo cp -R "$DEPLOY_DIR" "$BACKUP_PATH"
  echo "📦 Backup created: $BACKUP_PATH"
fi

# -----------------------------------
# 4. DEPLOY NEW BUILD
# -----------------------------------
echo "🚀 Deploying Angular build to $DEPLOY_DIR ..."

sudo rm -rf "$DEPLOY_DIR"           # Clear old deployment
sudo mkdir -p "$DEPLOY_DIR"
sudo cp -R "$BUILD_DIR/"* "$DEPLOY_DIR/"

sudo chown -R $RUN_USER:$RUN_USER "$DEPLOY_DIR"

echo "✅ Deployment completed."

# -----------------------------------
# 5. RESTART NGINX (if using web server)
# -----------------------------------
if systemctl list-units --type=service | grep -q "$NGINX_SERVICE"; then
  echo "🔄 Restarting NGINX..."
  sudo systemctl restart $NGINX_SERVICE

  if [ $? -eq 0 ]; then
    echo "✅ NGINX restarted successfully."
  else
    echo "⚠️ Warning: Failed to restart NGINX!"
  fi
else
  echo "ℹ️ NGINX not found. Skipping restart."
fi

# -----------------------------------
# 6. FINAL STATUS
# -----------------------------------
echo "🎉 Angular Deployment Completed Successfully!"
echo "🌐 Application Path: $DEPLOY_DIR"
