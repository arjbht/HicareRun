package com.ab.hicarerun.fragments;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.ab.hicarerun.BaseApplication;
import com.ab.hicarerun.BaseFragment;
import com.ab.hicarerun.R;
import com.ab.hicarerun.activities.HomeActivity;
import com.ab.hicarerun.activities.LoginActivity;
import com.ab.hicarerun.activities.VerifyOtpActivity;
import com.ab.hicarerun.databinding.FragmentFaceRecognizationBinding;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.AttendanceModel.AttendanceRequest;
import com.ab.hicarerun.network.models.AttendanceModel.ProfilePicRequest;
import com.ab.hicarerun.network.models.HandShakeModel.ContinueHandShakeResponse;
import com.ab.hicarerun.network.models.HandShakeModel.HandShake;
import com.ab.hicarerun.network.models.HandShakeModel.HandShakeResponse;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.service.ServiceLocationSend;
import com.ab.hicarerun.service.listner.LocationManagerListner;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.utils.FocusView;
import com.ab.hicarerun.utils.SharedPreferencesUtility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FaceRecognizationFragment extends BaseFragment implements SurfaceHolder.Callback, NetworkResponseListner {
    FragmentFaceRecognizationBinding mFragmentFaceRecognizationBinding;

    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private int cameraId;
    private static final String ARG_ATTENDANCE = "ARG_ATTENDANCE";
    private static final String ARG_USER = "ARG_USER";
    private boolean isAttendance = false;
    private int rotation;
    private String encodedImage = "";
    private static final int CAM_REQ = 1000;
    private static final int HANDSHAKE_REQUEST = 2000;
    private String username = "";


    public FaceRecognizationFragment() {
        // Required empty public constructor
    }

    public static FaceRecognizationFragment newInstance(boolean isAttendance, String username) {
        Bundle args = new Bundle();
        args.putBoolean(ARG_ATTENDANCE, isAttendance);
        args.putString(ARG_USER, username);
        FaceRecognizationFragment fragment = new FaceRecognizationFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            isAttendance = getArguments().getBoolean(ARG_ATTENDANCE);
            username = getArguments().getString(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentFaceRecognizationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_face_recognization, container, false);
        surfaceHolder = mFragmentFaceRecognizationBinding.surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        if (isAttendance) {
            mFragmentFaceRecognizationBinding.txtReason.setText("Please upload your photo to mark attendance.");
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            toolbar.setVisibility(View.GONE);
        }else {
            if((VerifyOtpActivity) getActivity() != null){
                mFragmentFaceRecognizationBinding.txtReason.setText("Please upload your photo to complete the registration.");
                Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
                toolbar.setVisibility(View.GONE);
            }
        }

        FocusView view = new FocusView(getActivity());
        mFragmentFaceRecognizationBinding.focusView.addView(view);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        return mFragmentFaceRecognizationBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        mFragmentFaceRecognizationBinding.captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeImage();
            }
        });
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT)) {
            alertCameraDialog();
        }
    }

    private void alertCameraDialog() {
        AlertDialog.Builder dialog = createAlert(getActivity(),
                "Camera info", "error to open camera");
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

            }
        });

        dialog.show();
    }

    private AlertDialog.Builder createAlert(Activity activity, String title, String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(
                new ContextThemeWrapper(activity,
                        android.R.style.Theme_Holo_Light_Dialog));
        dialog.setIcon(R.drawable.ic_mapicon);
        if (title != null)
            dialog.setTitle(title);
        else
            dialog.setTitle("Information");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        return dialog;

    }


    private boolean openCamera(int id) {
        boolean result = false;
        cameraId = id;
        releaseCamera();
        try {
            camera = Camera.open(cameraId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (camera != null) {
            try {
                setUpCamera(camera);
                camera.setErrorCallback(new Camera.ErrorCallback() {

                    @Override
                    public void onError(int error, Camera camera) {
//to show the error message.
                    }
                });
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
                result = true;
            } catch (IOException e) {
                e.printStackTrace();
                result = false;
                releaseCamera();
            }
        }
        return result;
    }

    private void releaseCamera() {
        try {
            if (camera != null) {
                camera.setPreviewCallback(null);
                camera.setErrorCallback(null);
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("error", e.toString());
            camera = null;
        }
    }

    //    Another important method in this is setUpCamera that gets the camera object as a parameter. In this method we manage the rotation and the flash button also because the front camera doesn't support a flash.
    private void setUpCamera(Camera c) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        int degree = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 0;
                break;
            case Surface.ROTATION_90:
                degree = 90;
                break;
            case Surface.ROTATION_180:
                degree = 180;
                break;
            case Surface.ROTATION_270:
                degree = 270;
                break;

            default:
                break;
        }

        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            // frontFacing
            rotation = (info.orientation + degree) % 330;
            rotation = (360 - rotation) % 360;
        } else {
            // Back-facing
            rotation = (info.orientation - degree + 360) % 360;
        }
        c.setDisplayOrientation(rotation);
        Camera.Parameters params = c.getParameters();

        List<String> focusModes = params.getSupportedFlashModes();
        if (focusModes != null) {
            if (focusModes
                    .contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
        }

        params.setRotation(rotation);
    }


    private void takeImage() {
        try {
            camera.takePicture(null, null, new Camera.PictureCallback() {

                private File imageFile;

                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    try {
                        // convert byte array into bitmap
                        Bitmap loadedImage = BitmapFactory.decodeByteArray(data, 0,
                                data.length);


                        // rotate Image
                        Matrix rotateMatrix = new Matrix();
                        rotateMatrix.postRotate(270);
                        Bitmap rotatedBitmap = Bitmap.createBitmap(loadedImage, 0,
                                0, loadedImage.getWidth(), loadedImage.getHeight(),
                                rotateMatrix, false);
                      Bitmap convertedImage =   getResizedBitmap(rotatedBitmap, 500);

                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        convertedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] b = baos.toByteArray();
                        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

                        if (isAttendance) {
                            if ((HomeActivity) getActivity() != null) {
                                RealmResults<LoginResponse> LoginRealmModels =
                                        BaseApplication.getRealm().where(LoginResponse.class).findAll();
                                if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                                    String BatteryStatistics = String.valueOf(AppUtils.getMyBatteryLevel(getActivity()));
                                    AttendanceRequest request = AppUtils.getDeviceInfo(getActivity(), encodedImage, BatteryStatistics, false);
                                    NetworkCallController controller = new NetworkCallController(FaceRecognizationFragment.this);
                                    controller.setListner(new NetworkResponseListner() {
                                        @Override
                                        public void onResponse(int requestCode, Object data) {
                                            ContinueHandShakeResponse response = (ContinueHandShakeResponse) data;
                                            if (response.getSuccess()) {
                                                Toast.makeText(getActivity(), "Attendance marked successfully.", Toast.LENGTH_LONG).show();
                                                replaceFragment(HomeFragment.newInstance(), "FaceRecognizationFragment-HomeFragment");
                                            } else {
                                                getErrorDialog("Attendance Failed", response.getErrorMessage());
                                            }
                                        }

                                        @Override
                                        public void onFailure(int requestCode) {

                                        }
                                    });
                                    controller.getTechAttendance(CAM_REQ, request);

                                }
                            }

                        } else {
                            if ((VerifyOtpActivity) getActivity() != null) {
                                RealmResults<LoginResponse> LoginRealmModels =
                                        BaseApplication.getRealm().where(LoginResponse.class).findAll();
                                String resourceId = LoginRealmModels.get(0).getUserID();
                                if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                                    NetworkCallController controller = new NetworkCallController(FaceRecognizationFragment.this);
                                    ProfilePicRequest request = new ProfilePicRequest();
                                    request.setProfilePic(encodedImage);
                                    request.setResourceId(resourceId);

                                    controller.setListner(new NetworkResponseListner() {
                                        @Override
                                        public void onResponse(int requestCode, Object data) {
                                            HandShakeResponse response = (HandShakeResponse) data;
                                            if (response.getSuccess()) {
                                                AppUtils.getHandShakeCall(username, getActivity());
                                            } else {
                                                getErrorDialog("Error", "Unable to capture your photo, please try again.");
                                            }
                                        }


                                        @Override
                                        public void onFailure(int requestCode) {

                                        }
                                    });
                                    controller.postResourceProfilePic(CAM_REQ, request);
                                }
                            }
                        }


                        String state = Environment.getExternalStorageState();
                        File folder = null;
                        if (state.contains(Environment.MEDIA_MOUNTED)) {
                            folder = new File(Environment
                                    .getExternalStorageDirectory() + "/hicare");
                        } else {
                            folder = new File(Environment
                                    .getExternalStorageDirectory() + "/hicare");
                        }

                        boolean success = true;
                        if (!folder.exists()) {
                            success = folder.mkdirs();
                        }
                        if (success) {
                            java.util.Date date = new java.util.Date();
                            imageFile = new File(folder.getAbsolutePath()
                                    + File.separator
                                    + new Timestamp(date.getTime()).toString()
                                    + "Image.jpg");

                            imageFile.createNewFile();
//                        replaceFragment(HomeFragment.newInstance(), "FaceRecognizationFragment-HomeFragment");

                        } else {
                            Toast.makeText(getActivity(), "Image Not saved",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ByteArrayOutputStream ostream = new ByteArrayOutputStream();

                        // save image into gallery
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);

                        FileOutputStream fout = new FileOutputStream(imageFile);
                        fout.write(ostream.toByteArray());
                        fout.close();
                        ContentValues values = new ContentValues();

                        values.put(MediaStore.Images.Media.DATE_TAKEN,
                                System.currentTimeMillis());
                        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                        values.put(MediaStore.MediaColumns.DATA,
                                imageFile.getAbsolutePath());


                        getActivity().getContentResolver().insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("error1", e.getMessage());
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("error2", e.getMessage());
        }


    }



    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }


    @Override
    public void onResponse(int requestCode, Object response) {

    }

    @Override
    public void onFailure(int requestCode) {

    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void getErrorDialog(String title, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage(
                    message);
            builder.setPositiveButton("Try again", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    openCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    dialogInterface.dismiss();
                }
            }).create().show();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("handshake", e.getMessage());
        }
    }




}
