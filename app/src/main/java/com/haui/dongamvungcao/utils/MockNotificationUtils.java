package com.haui.dongamvungcao.utils;

import android.content.Context;
import android.telephony.SmsManager;
import android.widget.Toast;

public class MockNotificationUtils {

    /**
     * HÀM GỬI TIN NHẮN THẬT / GIẢ LẬP TRÊN MÁY ẢO BẰNG SMS_MANAGER
     */
    public static void simulateSmsMessage(Context context, String phoneNumber, String messageBody) {
        // Nếu số điện thoại rỗng thì lấy số mặc định để tránh lỗi biên dịch
        final String targetPhone = (phoneNumber == null || phoneNumber.isEmpty()) ? "0987654321" : phoneNumber;

        // Loại bỏ dấu tiếng Việt để tin nhắn hiển thị chuẩn định dạng viễn thông
        final String safeBody = removeAccent(messageBody);

        try {
            // Sử dụng SmsManager chuẩn của Android để phát tín hiệu gửi tin nhắn
            SmsManager smsManager = SmsManager.getDefault();

            // Hàm gửi tin nhắn văn bản tự động đến chính số điện thoại đăng ký
            smsManager.sendTextMessage(targetPhone, null, safeBody, null, null);

            System.out.println("--- ĐÃ PHÁT TÍN HIỆU GỬI SMS QUA SMS_MANAGER ĐẾN SỐ: " + targetPhone);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("--- LỖI PHÁT SMS_MANAGER: " + e.getMessage());

            // PHƯƠNG ÁN DỰ PHÒNG HOÀN HẢO: Nếu máy ảo lỗi driver sim sóng,
            // Tự động mở màn hình soạn thảo tin nhắn hệ thống và điền sẵn số + nội dung để bạn bấm Gửi luôn
            try {
                android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_SENDTO);
                intent.setData(android.net.Uri.parse("smsto:" + targetPhone));
                intent.putExtra("sms_body", safeBody);
                intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Hàm hỗ trợ tự động xóa dấu tiếng Việt để hệ thống SMS chạy mượt mà
     */
    private static String removeAccent(String str) {
        if (str == null) return "";
        String nfdForm = java.text.Normalizer.normalize(str, java.text.Normalizer.Form.NFD);
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdForm).replaceAll("")
                .replaceAll("Đ", "D")
                .replaceAll("đ", "d")
                .replaceAll("🎉", "")
                .replaceAll("📝", "");
    }
}