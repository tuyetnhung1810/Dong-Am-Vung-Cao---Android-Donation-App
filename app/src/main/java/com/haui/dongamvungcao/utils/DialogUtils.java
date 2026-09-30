package com.haui.dongamvungcao.utils;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class DialogUtils {

    // Giao diện chức năng (Interface) để xử lý sự kiện sau khi người dùng bấm OK
    public interface OnDialogClickListener {
        void onClick();
    }

    /**
     * Hiển thị hộp thoại thông báo thành công khi quyên góp
     * @param context Ngữ cảnh hiển thị (thường là Activity)
     * @param message Lời nhắn cảm ơn hiển thị trên Dialog
     * @param listener Hành động xử lý sau khi đóng Dialog (ví dụ: tắt màn hình)
     */
    public static void showSuccessDialog(Context context, String message, final OnDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("Đông Ấm Vùng Cao HaUI")
                .setMessage(message)
                .setCancelable(false) // Buộc người dùng phải ấn nút để đóng, tránh ấn nhầm ra ngoài
                .setPositiveButton("Đồng ý", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onClick(); // Kích hoạt sự kiện tiếp theo (finish activity)
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}