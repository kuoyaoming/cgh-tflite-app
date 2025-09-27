package com.cgh.org.audio.Interface;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

/**
 * Live Updates Manager for Android 16
 * Manages real-time notifications and updates
 */
public class LiveUpdatesManager {
    
    private static final String CHANNEL_ID = "live_updates_channel";
    private static final String CHANNEL_NAME = "Live Updates";
    private static final String CHANNEL_DESCRIPTION = "Real-time audio processing updates";
    
    private Context context;
    private NotificationManager notificationManager;
    private NotificationManagerCompat notificationManagerCompat;
    
    public LiveUpdatesManager(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.notificationManagerCompat = NotificationManagerCompat.from(context);
        createNotificationChannel();
    }
    
    /**
     * Create notification channel for Android 8.0+
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    /**
     * Show live update notification for audio recording
     */
    public void showRecordingUpdate(String status, int progress) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("Audio Recording")
            .setContentText(status)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true);
        
        // Add Android 16 specific features
        if (Build.VERSION.SDK_INT >= 36) {
            // Add live update features
            builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText("Recording in progress... " + status + "\nProgress: " + progress + "%"));
            
            // Add action buttons
            builder.addAction(android.R.drawable.ic_media_pause, "Pause", 
                createActionIntent("pause_recording"));
            builder.addAction(android.R.drawable.ic_media_pause, "Stop", 
                createActionIntent("stop_recording"));
        }
        
        notificationManagerCompat.notify(1, builder.build());
    }
    
    /**
     * Show live update notification for audio analysis
     */
    public void showAnalysisUpdate(String status, int progress) {
        Intent intent = new Intent(context, ResultActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 1, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setContentTitle("Audio Analysis")
            .setContentText(status)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true);
        
        // Add Android 16 specific features
        if (Build.VERSION.SDK_INT >= 36) {
            // Add live update features
            builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText("Analyzing audio... " + status + "\nProgress: " + progress + "%"));
            
            // Add action buttons
            builder.addAction(android.R.drawable.ic_menu_view, "View Results", 
                createActionIntent("view_results"));
            builder.addAction(android.R.drawable.ic_menu_share, "Share", 
                createActionIntent("share_results"));
        }
        
        notificationManagerCompat.notify(2, builder.build());
    }
    
    /**
     * Show live update notification for file upload
     */
    public void showUploadUpdate(String status, int progress) {
        Intent intent = new Intent(context, UploaderActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 2, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setContentTitle("File Upload")
            .setContentText(status)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true);
        
        // Add Android 16 specific features
        if (Build.VERSION.SDK_INT >= 36) {
            // Add live update features
            builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText("Uploading file... " + status + "\nProgress: " + progress + "%"));
            
            // Add action buttons
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Cancel", 
                createActionIntent("cancel_upload"));
            builder.addAction(android.R.drawable.ic_menu_info_details, "Details", 
                createActionIntent("upload_details"));
        }
        
        notificationManagerCompat.notify(3, builder.build());
    }
    
    /**
     * Show completion notification
     */
    public void showCompletionNotification(String title, String message, String action) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setOnlyAlertOnce(false);
        
        // Add Android 16 specific features
        if (Build.VERSION.SDK_INT >= 36) {
            // Add live update features
            builder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(message));
            
            // Add action button
            builder.addAction(android.R.drawable.ic_menu_view, action, 
                createActionIntent(action.toLowerCase().replace(" ", "_")));
        }
        
        notificationManagerCompat.notify(4, builder.build());
    }
    
    /**
     * Create action intent for notification buttons
     */
    private PendingIntent createActionIntent(String action) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("action", action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        return PendingIntent.getActivity(
            context, 
            action.hashCode(), 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }
    
    /**
     * Cancel specific notification
     */
    public void cancelNotification(int notificationId) {
        notificationManagerCompat.cancel(notificationId);
    }
    
    /**
     * Cancel all notifications
     */
    public void cancelAllNotifications() {
        notificationManagerCompat.cancelAll();
    }
    
    /**
     * Update notification progress
     */
    public void updateProgress(int notificationId, int progress, String status) {
        // This would be implemented to update existing notifications
        // For now, we'll create a new notification with updated progress
        if (notificationId == 1) {
            showRecordingUpdate(status, progress);
        } else if (notificationId == 2) {
            showAnalysisUpdate(status, progress);
        } else if (notificationId == 3) {
            showUploadUpdate(status, progress);
        }
    }
}