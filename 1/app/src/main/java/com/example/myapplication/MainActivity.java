package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.File;

@RequiresApi(api = Build.VERSION_CODES.R)
public class MainActivity extends AppCompatActivity {

    private static boolean officialDirAuthed = false;
    private static boolean bilibiliDirAuthed = false;


    //读写权限
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
    };

    //请求状态码
    private static int REQUEST_PERMISSION_CODE = 1;

    public static final String OFFICIAL_NAME = "com.miHoYo.Yuanshen";
    public static final String BILIBILI_NAME = "com.miHoYo.ys.bilibili";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide(); //隐藏标题栏
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
        // android 11 申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 先判断有没有权限
            if (!Environment.isExternalStorageManager()) {
                Toast.makeText(this, "请通过权限！", Toast.LENGTH_SHORT);
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1024);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }
    public static boolean checkApkExist(Context context, String packageName){
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            context.getPackageManager().getApplicationInfo(packageName,PackageManager.MATCH_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void startOfficial(View view) {
        if(!checkApkExist(this, OFFICIAL_NAME)) {
            Toast.makeText(this, "未安装官方版", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("https://www.taptap.com/app/168332");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return;
        }
        String externalStoragePath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/";
        String officialDirName = externalStoragePath + OFFICIAL_NAME;
        String bilibiliDirName = externalStoragePath + BILIBILI_NAME;
        Log.i("MainActivity", externalStoragePath);
        File officialDir = new File(officialDirName);
        File bilibiliDir = new File(bilibiliDirName);
        if (!officialDir.exists() && bilibiliDir.exists()) {
            Log.i("MainActivity", "exists");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if(bilibiliDir.exists() && !bilibiliDirAuthed) {
                    Toast.makeText(this, "请通过权限后再次点击启动按钮！", Toast.LENGTH_SHORT).show();
                    startFor(bilibiliDirName, 11);
                    return;
                }
                DocumentFile documentFile = DocumentFile.fromTreeUri(this, Uri.parse(changeToUri3(bilibiliDirName)));
                System.out.println(documentFile.renameTo(OFFICIAL_NAME));
            } else {
                System.out.println(bilibiliDir.renameTo(officialDir));
            }
        }
        Intent intent = new Intent();
        intent.setClassName(OFFICIAL_NAME, "com.miHoYo.GetMobileInfo.MainActivity");
        startActivity(intent);
    }

    public void startBilibili(View view) {
        if(!checkApkExist(this, BILIBILI_NAME)) {
            Toast.makeText(this, "未安装Bilibili版", Toast.LENGTH_SHORT).show();
            Uri uri = Uri.parse("https://app.biligame.com/page/detail_share.html?id=103496&sourceFrom=1112&action=1");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
            return;
        }
        String externalStoragePath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/";
        String officialDirName = externalStoragePath + OFFICIAL_NAME;
        String bilibiliDirName = externalStoragePath + BILIBILI_NAME;
        File officialDir = new File(officialDirName);
        File bilibiliDir = new File(bilibiliDirName);
        if (!bilibiliDir.exists() && officialDir.exists()) {
            Log.i("MainActivity", "exists");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if(officialDir.exists() && !officialDirAuthed) {
                    Toast.makeText(this, "请通过权限后再次点击启动按钮！", Toast.LENGTH_SHORT).show();
                    startFor(officialDirName, 10);
                    return;
                }
                DocumentFile documentFile = DocumentFile.fromTreeUri(this, Uri.parse(changeToUri3(officialDirName)));
                System.out.println(documentFile.renameTo(BILIBILI_NAME));
            } else {
                System.out.println(officialDir.renameTo(bilibiliDir));
            }

        }
        Intent intent = new Intent();
        intent.setClassName(BILIBILI_NAME, "com.miHoYo.GetMobileInfo.MainActivity");
        startActivity(intent);
    }
    public boolean deleteofficial() {
        String externalStoragePath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/";
        String officialDirName = externalStoragePath + OFFICIAL_NAME;
        if(!officialDirAuthed) {
            Toast.makeText(this, "请通过权限后再次点击启动按钮！", Toast.LENGTH_SHORT).show();
            startFor(officialDirName, 10);
            return false;
        }
        DocumentFile officialDir = DocumentFile.fromTreeUri(this, Uri.parse(changeToUri3(officialDirName)));
        Toast.makeText(this, "官方版资源文件删除成功！", Toast.LENGTH_SHORT).show();
        officialDir.delete();
        return false;
    }
    public boolean deletebilibili() {
        String externalStoragePath = Environment.getExternalStorageDirectory().getPath() + "/Android/data/";
        String bilibiliDirName = externalStoragePath + BILIBILI_NAME;
        if(!bilibiliDirAuthed) {
            Toast.makeText(this, "请通过权限后再次点击启动按钮！", Toast.LENGTH_SHORT).show();
            startFor(bilibiliDirName, 11);
            return false;
        }
        DocumentFile bilibiliDir = DocumentFile.fromTreeUri(this, Uri.parse(changeToUri3(bilibiliDirName)));
        Toast.makeText(this, "Bilibili资源文件删除成功！", Toast.LENGTH_SHORT).show();
        bilibiliDir.delete();
        return false;
    }
    public void startdeleteofficial(View view) {

        new AlertDialog.Builder(this).setTitle("确认是否删除？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        deleteofficial();

                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();





    }
    public void startdeletebilibli(View view) {
        new AlertDialog.Builder(this).setTitle("确认是否删除？")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“确认”后的操作
                        deletebilibili();

                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 点击“返回”后的操作,这里不设置没有任何操作
                    }
                }).show();


        }




    @TargetApi(26)
    public void startFor(String path, int code) {
        String uri = changeToUri(path);
        Uri parse = Uri.parse(uri);
        Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
        intent.addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                        | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, parse);
        }
        this.startActivityForResult(intent, code);

    }

    public static String changeToUri(String path) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        String path2 = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3A" + path2;
    }

    //转换至uriTree的路径
    public static String changeToUri3(String path) {
        path = path.replace("/storage/emulated/0/", "").replace("/", "%2F");
        return ("content://com.android.externalstorage.documents/tree/primary%3A" + path);

    }

    //返回授权状态
    @SuppressLint("WrongConstant")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri uri;

        if (data == null) {
            return;
        }

        if ((uri = data.getData()) != null) {
            getContentResolver().takePersistableUriPermission(uri, data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION));//关键是这里，这个就是保存这个目录的访问权限
            if(requestCode == 10) {
                Toast.makeText(this, "Bilibili授权成功！", Toast.LENGTH_SHORT).show();
                officialDirAuthed = true;
            }
            if(requestCode == 11) {
                Toast.makeText(this, "官方版授权成功！", Toast.LENGTH_SHORT).show();
                bilibiliDirAuthed = true;
            }
        }

    }
}
