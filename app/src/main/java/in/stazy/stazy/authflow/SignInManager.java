package in.stazy.stazy.authflow;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Document;

import java.util.HashMap;
import java.util.Map;

import in.stazy.stazy.datamanagercrossend.HotelData;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.datamanagerperformer.PerformerData;
import in.stazy.stazy.datamanagerperformer.PerformerManager;
import in.stazy.stazy.hotelend.MainActivityHotel;
import in.stazy.stazy.performerend.MainActivityPerformer;

public class SignInManager {

    private static Context varContext;
    private static SignInCommunication communication;

    public static void signInWithNewCredentials(PhoneAuthCredential credential, final Context context, final Map data, final Uri profilePicture, final String city) {
        varContext = context;
        communication = (SignInCommunication) context;
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Manager.CITY_VALUE = city;
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            data.put("token", task.getResult().getToken());
                            Manager.FCM_TOKEN = task.getResult().getToken();
                            if (SignUpActivity.automaticVerification != null) {
                                SignUpActivity.automaticVerification.dismiss();
                                SignUpActivity.automaticVerification = null;
                            }
                            if (SignUpActivity.otpWaitFragmentHotel != null) {
                                SignUpActivity.otpWaitFragmentHotel.dismiss();
                                SignUpActivity.otpWaitFragmentHotel = null;
                            }
                            if (SignUpActivity.otpWaitFragmentPerformer != null) {
                                SignUpActivity.otpWaitFragmentPerformer.dismiss();
                                SignUpActivity.otpWaitFragmentPerformer = null;
                            }
                            if (SignUpActivity.INITIAL_SELECTION == SignUpActivity.HOTEL)
                                uploadDataAndStartUIHotel(data, profilePicture);
                            else
                                uploadDataAndStartUIPerformer(data, profilePicture);
                        }
                    });
                } else {
                    // Sign in failed, display a message and update the UI
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        Toast.makeText(context, "Invalid OTP Detected, Try Again Later", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private static void uploadDataAndStartUIPerformer(Map data, Uri profilePicture) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child(FirebaseAuth.getInstance().getUid()+"/"+data.get("pic_name"));
        storageReference.putFile(profilePicture).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                    Log.e("PIC UPLOAD HOTEL", "success");
                else
                    Log.e("PIC UPLOAD HOTEL", task.getException().getMessage());
            }
        });

        data.put("uid", FirebaseAuth.getInstance().getUid());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                                .collection("type").document(PerformerManager.TYPE_VALUE)
                                .collection(PerformerManager.GENRE_VALUE).document(FirebaseAuth.getInstance().getUid());
        documentReference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.e("UPLOAD DATA PERFORMER", "Success");
                else
                    Log.e("UPLOAD DATA PERFORMER", task.getException().getMessage());
            }
        });

        DocumentReference mapperReference = firebaseFirestore.collection("Mapper").document(FirebaseAuth.getInstance().getUid());
        Map<String, String> mapperData = new HashMap<>();
        mapperData.put("city", Manager.CITY_VALUE);
        mapperData.put("category", "PERFORMER");
        mapperData.put("type", PerformerManager.TYPE_VALUE);
        mapperData.put("genre", PerformerManager.GENRE_VALUE);

        mapperReference.set(mapperData);

        PerformerManager.PERFORMER = PerformerData.setDataOnSignUp(data);
        Intent intent = new Intent(varContext, MainActivityPerformer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        communication.fireIntentActivity(intent);

    }

    private static void uploadDataAndStartUIHotel(Map data, Uri profilePicture) {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child(FirebaseAuth.getInstance().getUid()+"/"+data.get("pic_name"));
        storageReference.putFile(profilePicture).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful())
                    Log.e("PIC UPLOAD HOTEL", "success");
                else
                    Log.e("PIC UPLOAD HOTEL", task.getException().getMessage());
            }
        });

        data.put("uid", FirebaseAuth.getInstance().getUid());
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = firebaseFirestore.collection("Cities").document(Manager.CITY_VALUE)
                        .collection("hotels").document(FirebaseAuth.getInstance().getUid());
        documentReference.set(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                    Log.e("UPLOAD DATA HOTEL", "Success");
                else
                    Log.e("UPLOAD DATA HOTEL", task.getException().getMessage());
            }
        });

        DocumentReference mapperReference = firebaseFirestore.collection("Mapper").document(FirebaseAuth.getInstance().getUid());
        Map<String, String> mapperData = new HashMap<>();
        mapperData.put("city", Manager.CITY_VALUE);
        mapperData.put("category", "HOTEL");

        mapperReference.set(mapperData);

        Manager.HOTEL_DATA = HotelData.setDataOnSignUp(data);
        Intent intent = new Intent(varContext, MainActivityHotel.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        communication.fireIntentActivity(intent);
    }

    public static void signInWithOldCredentials(PhoneAuthCredential credential, Context context) {
        varContext = context;
        communication = (SignInCommunication) context;

        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    if (SignInActivity.verificationFragment != null) {
                        SignInActivity.verificationFragment.dismiss();
                        SignInActivity.verificationFragment = null;
                    }
                    if (SignInActivity.signInFragment != null) {
                        SignInActivity.signInFragment.dismiss();
                        SignInActivity.signInFragment = null;
                    }
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    DocumentReference mapperReference = firebaseFirestore.collection("Mapper").document(FirebaseAuth.getInstance().getUid());
                    mapperReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            Manager.CITY_VALUE = documentSnapshot.get("city").toString();
                            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    InstanceIdResult result = task.getResult();
                                    Manager.FCM_TOKEN = result.getToken();
                                }
                            });
                            if (documentSnapshot.get("category").toString().equals("PERFORMER")) {
                                PerformerManager.TYPE_VALUE = documentSnapshot.get("type").toString();
                                PerformerManager.GENRE_VALUE = documentSnapshot.get("genre").toString();
                                startUIPerformer();
                            } else {
                                startUIHotel();
                            }
                        }
                    });
                }
            }
        });

    }

    private static void startUIHotel() {
        Intent intent = new Intent(varContext, MainActivityHotel.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        communication.fireIntentActivity(intent);
    }

    private static void startUIPerformer() {
        Intent intent = new Intent(varContext, MainActivityPerformer.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        communication.fireIntentActivity(intent);
    }

    interface SignInCommunication {
        void fireIntentActivity(Intent intent);
    }

}
