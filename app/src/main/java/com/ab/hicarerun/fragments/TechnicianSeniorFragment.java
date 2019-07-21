package com.ab.hicarerun.fragments;


import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ab.hicarerun.BaseApplication;
import com.ab.hicarerun.BaseFragment;
import com.ab.hicarerun.R;
import com.ab.hicarerun.activities.GrommingGalleryActivity;
import com.ab.hicarerun.activities.HomeActivity;
import com.ab.hicarerun.activities.TechnicianSeniorActivity;
import com.ab.hicarerun.adapter.ReferralListAdapter;
import com.ab.hicarerun.adapter.TechnicianGroomingAdapter;
import com.ab.hicarerun.databinding.FragmentTechnicianSeniorBinding;
import com.ab.hicarerun.handler.OnCaptureListItemClickHandler;
import com.ab.hicarerun.network.NetworkCallController;
import com.ab.hicarerun.network.NetworkResponseListner;
import com.ab.hicarerun.network.models.BasicResponse;
import com.ab.hicarerun.network.models.Item;
import com.ab.hicarerun.network.models.LoginResponse;
import com.ab.hicarerun.network.models.ReferralModel.ReferralList;
import com.ab.hicarerun.network.models.TechnicianGroomingModel.TechGroom;
import com.ab.hicarerun.network.models.TechnicianGroomingModel.TechGroomingRequest;
import com.ab.hicarerun.utils.AppUtils;
import com.ab.hicarerun.viewmodel.GroomingViewModel;
import com.mindorks.paracamera.Camera;
import com.vansuita.pickimage.bundle.PickSetup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

/**
 * A simple {@link Fragment} subclass.
 */
public class TechnicianSeniorFragment extends BaseFragment implements OnCaptureListItemClickHandler {
    FragmentTechnicianSeniorBinding mFragmentTechnicianSeniorBinding;
    RecyclerView.LayoutManager layoutManager;
    TechnicianGroomingAdapter mAdapter;
    private static final int GET_GROOMING_REQ = 1000;
    private static final int POST_GROOMING_REQ = 2000;
    private Integer pageNumber = 1;
    private static final int CAMERA_REQUEST = 100;
    private int pos = 0;
    private Camera camera;
    private static final String IMAGE_DIRECTORY = "/hicare";
    final String FILE_ENTENSION = ".jpg";
    public String IMAGE_FILE_PATH;
    public static int count = 0;
    String tempImageName = "";
    String TimeStamp = "";

    public TechnicianSeniorFragment() {
        // Required empty public constructor
    }

    public static TechnicianSeniorFragment newInstance() {
        Bundle args = new Bundle();
        TechnicianSeniorFragment fragment = new TechnicianSeniorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentTechnicianSeniorBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_technician_senior, container, false);
        getActivity().setTitle("Grooming");
        return mFragmentTechnicianSeniorBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFragmentTechnicianSeniorBinding.swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        getGroomingDetails();
                    }
                });
        mFragmentTechnicianSeniorBinding.recycleView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        mFragmentTechnicianSeniorBinding.recycleView.setLayoutManager(layoutManager);
        mFragmentTechnicianSeniorBinding.swipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_blue_dark, android.R.color.holo_blue_light,
                android.R.color.holo_red_dark, android.R.color.holo_red_light,
                android.R.color.holo_green_dark, android.R.color.holo_green_light,
                android.R.color.holo_red_dark, android.R.color.holo_red_light);
        mAdapter = new TechnicianGroomingAdapter(getActivity());
        mAdapter.setOnItemClickHandler(this);
        mFragmentTechnicianSeniorBinding.recycleView.setAdapter(mAdapter);
        getGroomingDetails();
        mFragmentTechnicianSeniorBinding.swipeRefreshLayout.setRefreshing(true);
        IMAGE_FILE_PATH = Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY;
        File directory = new File(
                Environment.getExternalStorageDirectory()
                        + IMAGE_DIRECTORY);
        if (directory.exists()) {
            File[] contents = directory.listFiles();
            if (contents != null) {
                if (contents.length > 0) {

                    for (File content : contents) {
                        if (content.isFile()) {
                            File sourceFile = new File(content.toString());
                            sourceFile.delete();
                        }

                    }
                }
            }
        } else {
            directory.mkdir();
        }

    }

    private void getGroomingDetails() {
        if ((TechnicianSeniorActivity) getActivity() != null) {
            RealmResults<LoginResponse> LoginRealmModels =
                    BaseApplication.getRealm().where(LoginResponse.class).findAll();
            if (LoginRealmModels != null && LoginRealmModels.size() > 0) {
                String userId = LoginRealmModels.get(0).getUserID();
                NetworkCallController controller = new NetworkCallController(this);
                controller.setListner(new NetworkResponseListner() {
                    @Override
                    public void onResponse(int requestCode, Object data) {
                        List<TechGroom> items = (List<TechGroom>) data;
                        mFragmentTechnicianSeniorBinding.swipeRefreshLayout.setRefreshing(false);
                        if (items != null) {
                            if (pageNumber == 1 && items.size() > 0) {
                                mAdapter.setData(items);
                                mAdapter.notifyDataSetChanged();
                            } else if (items.size() > 0) {
                                mAdapter.addData(items);
                                mAdapter.notifyDataSetChanged();
                            } else {
                                pageNumber--;
                            }
                        }
                    }

                    @Override
                    public void onFailure(int requestCode) {

                    }
                });
                controller.getGroomingTechnicians(GET_GROOMING_REQ, userId);
            }
        }
    }


    @Override
    public void onCaptureImageItemClick(int position) {
        pos = position;
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        tempImageName = createFileName() + getFileExtension();
        File photo = new File(IMAGE_FILE_PATH, tempImageName);
        Log.i("photo", String.valueOf(photo));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".provider", photo);
            Log.i("provider", String.valueOf(photoURI));
            intent.putExtra("output", photoURI);
        } else {
            intent.putExtra("output", Uri.fromFile(photo));
        }

        TimeStamp = AppUtils.getCurrentTimeStamp();
        startActivityForResult(intent, CAMERA_REQUEST);
        Uri.fromFile(photo);

    }

    @Override
    public void onViewImageItemClick(int position) {
        Intent intent = new Intent(getActivity(), GrommingGalleryActivity.class);
        intent.putExtra("Image", mAdapter.getItem(position).getImageUrl());
        intent.putExtra("Title", mAdapter.getItem(position).getTechnicianName());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Item prescriptionImage;
            Bitmap selectedImageBitmap = null;

//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            Bitmap newImage = getResizedBitmap(image, 2000);
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//            byte[] b = baos.toByteArray();
//            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
//            uploadCapturedImage(encodedImage);


            try {
                prescriptionImage = getCapturedImage(requestCode);
                Log.i("presImage", String.valueOf(prescriptionImage));
                if (prescriptionImage.getPath().length() > 0) {
                    Uri mImageUri = Uri.fromFile(new File(prescriptionImage.getPath()));
                    Log.i("imagepath", String.valueOf(mImageUri));

//                    String path = prescriptionImage.getPath();
                    try {
                        selectedImageBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), mImageUri);
                        selectedImageBitmap = getResizedBitmap(selectedImageBitmap, 1000);

                        ExifInterface ei = new ExifInterface(prescriptionImage.getPath());
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);

                        switch (orientation) {

                            case ExifInterface.ORIENTATION_ROTATE_90:
                                selectedImageBitmap = rotateImage(selectedImageBitmap, 90);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_180:
                                selectedImageBitmap = rotateImage(selectedImageBitmap, 180);
                                break;

                            case ExifInterface.ORIENTATION_ROTATE_270:
                                selectedImageBitmap = rotateImage(selectedImageBitmap, 270);
                                break;

                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                selectedImageBitmap = selectedImageBitmap;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    if (selectedImageBitmap != null) {
                        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
                        byte[] b = byteArrayOutputStream.toByteArray();
                        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
                        uploadCapturedImage(encodedImage);
                    }

                } else {
                    Toast.makeText(getActivity(), "No image captured", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception ignored) {
                ignored.printStackTrace();
            }


        }


    }


    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public Item getCapturedImage(int requestCode) {

        Item myClass = null;
        String picturePath;
        if (requestCode == CAMERA_REQUEST) {
            try {
                myClass = new Item();
                File f = new File(IMAGE_FILE_PATH + "/" + tempImageName);
                picturePath = f.getAbsolutePath();

                if (!f.exists()) {
                    picturePath = "";
                }
                myClass.setPath(picturePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return myClass;
    }


    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }


    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("camera", "Oops! Failed create "
                        + IMAGE_DIRECTORY + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;

        if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void uploadCapturedImage(String encodedImage) {
        NetworkCallController controller = new NetworkCallController(this);
        TechGroomingRequest request = new TechGroomingRequest();
        request.setEmployeeCode(mAdapter.getItem(pos).getEmployeeCode());
        request.setTechnicianId(mAdapter.getItem(pos).getTechnicianId());
        request.setTechnicianName(mAdapter.getItem(pos).getTechnicianName());
        request.setMobileNo(mAdapter.getItem(pos).getMobileNo());
        request.setImage(encodedImage);
        controller.setListner(new NetworkResponseListner() {
            @Override
            public void onResponse(int requestCode, Object data) {
                BasicResponse response = (BasicResponse) data;
                if (response.getSuccess()) {
//                    Toast.makeText(getActivity(), "Image uploaded successfully.", Toast.LENGTH_SHORT).show();
                    Toasty.success(getActivity(),"Image uploaded successfully.",Toasty.LENGTH_SHORT).show();
                    getGroomingDetails();
                } else {
                    Toast.makeText(getActivity(), "Failed.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int requestCode) {

            }
        });
        controller.postGroomingImage(POST_GROOMING_REQ, request);

    }

    @Override
    public void onItemClick(int positon) {

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

    public String createFileName() {
        return "" + System.currentTimeMillis();
    }

    public String getFileExtension() {
        return FILE_ENTENSION;
    }
}
