package com.androidfacedetection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.io.IOException;

public class FaceDetectionActivity extends AppCompatActivity {

    private static final String TAG = "FaceDetection";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_detection);

        final FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .build();

        Intent intent = getIntent();
        final ImageView imageView = findViewById(R.id.imageView);
        final String mCurrentPhotoPath = intent.getStringExtra("mCurrentPhotoPath");

        // run image related code after the view was laid out
        // to have all dimensions calculated
        imageView.post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentPhotoPath != null) {
                    Bitmap bitmap = getBitmapFromPathForImageView(mCurrentPhotoPath, imageView);
                    imageView.setImageBitmap(bitmap);

                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<Face> faces = detector.detect(frame);

                    Log.d(TAG, "Faces detected: " + String.valueOf(faces.size()));

                    Paint paint = new Paint();
                    paint.setColor(Color.GREEN);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(5);

                    Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    Canvas canvas = new Canvas(mutableBitmap);

                    for (int i = 0; i < faces.size(); ++i) {
                        Face face = faces.valueAt(i);
                        for (Landmark landmark : face.getLandmarks()) {
                            int cx = (int) (landmark.getPosition().x);
                            int cy = (int) (landmark.getPosition().y);
                            canvas.drawCircle(cx, cy, 10, paint);
                        }

                        double[][] parameters = getParameters(face);

                        ComputingParameters.setAreas(parameters[0]);
                        ComputingParameters.setAngles(parameters[1]);

                        Path path = new Path();
                        path.moveTo(face.getPosition().x, face.getPosition().y);
                        path.lineTo(face.getPosition().x + face.getWidth(), face.getPosition().y);
                        path.lineTo(face.getPosition().x + face.getWidth(), face.getPosition().y + face.getHeight());
                        path.lineTo(face.getPosition().x, face.getPosition().y + face.getHeight());
                        path.close();

                        Paint redPaint = new Paint();
                        redPaint.setColor(0XFFFF0000);
                        redPaint.setStyle(Paint.Style.STROKE);
                        redPaint.setStrokeWidth(8.0f);
                        canvas.drawPath(path, redPaint);
                    }

                    imageView.setImageBitmap(mutableBitmap);
                }
            }
        });
    }

    private double[][] getParameters(Face face) {
        FaceLandmarks LE = new FaceLandmarks();
        FaceLandmarks RE = new FaceLandmarks();
        FaceLandmarks Le = new FaceLandmarks();
        FaceLandmarks Re = new FaceLandmarks();
        FaceLandmarks N = new FaceLandmarks();
        FaceLandmarks LM = new FaceLandmarks();
        FaceLandmarks RM = new FaceLandmarks();
        for (Landmark landmark : face.getLandmarks()) {
            int cx = (int) (landmark.getPosition().x);
            int cy = (int) (landmark.getPosition().y);

            if (landmark.getType() == 1) {
                LE.setxPos(cx);
                LE.setyPos(cy);
                LE.setLandmarkId(landmark.getType());
            } else if (landmark.getType() == 7) {
                RE.setxPos(cx);
                RE.setyPos(cy);
                RE.setLandmarkId(landmark.getType());
            } else if (landmark.getType() == 4) {
                Le.setxPos(cx);
                Le.setyPos(cy);
                Le.setLandmarkId(landmark.getType());
            } else if (landmark.getType() == 6) {
                N.setxPos(cx);
                N.setyPos(cy);
                N.setLandmarkId(landmark.getType());
            } else if (landmark.getType() == 10) {
                Re.setxPos(cx);
                Re.setyPos(cy);
                Re.setLandmarkId(landmark.getType());
            } else if (landmark.getType() == 5) {
                LM.setxPos(cx);
                LM.setyPos(cy);
                LM.setLandmarkId(landmark.getType());
            } else if (landmark.getType() == 11) {
                RM.setxPos(cx);
                RM.setyPos(cy);
                RM.setLandmarkId(landmark.getType());
            }
        }

        double acEe = Math.abs(Le.getyPos() - LE.getyPos());
        double cbEe = Math.abs(Le.getxPos() - LE.getxPos());

        double LELe = (Math.hypot(acEe, cbEe));

        double acEN = Math.abs(N.getyPos() - LE.getyPos());
        double cbEN = Math.abs(N.getxPos() - LE.getxPos());

        double LEN = (Math.hypot(acEN, cbEN));

        double acEM = Math.abs(LM.getyPos() - LE.getyPos());
        double cbEM = Math.abs(LM.getxPos() - LE.getxPos());

        double LELM = (Math.hypot(acEM, cbEM));

        double aceN = Math.abs(N.getyPos() - Le.getyPos());
        double cbeN = Math.abs(N.getxPos() - Le.getxPos());

        double LeN = (Math.hypot(aceN, cbeN));

        double acee = Math.abs(Re.getyPos() - Le.getyPos());
        double cbee = Math.abs(Re.getxPos() - Le.getxPos());

        double LeRe = (Math.hypot(acee, cbee));

        double aceE = Math.abs(RE.getyPos() - Re.getyPos());
        double cbeE = Math.abs(RE.getxPos() - Re.getxPos());

        double ReRE = (Math.hypot(aceE, cbeE));

        double aceM = Math.abs(Re.getyPos() - RM.getyPos());
        double cbeM = Math.abs(Re.getxPos() - RM.getxPos());

        double ReRM = (Math.hypot(aceM, cbeM));

        double aceNL = Math.abs(Re.getyPos() - N.getyPos());
        double cbeNL = Math.abs(Re.getxPos() - N.getxPos());

        double ReN = (Math.hypot(aceNL, cbeNL));

        double acEME = Math.abs(RE.getyPos() - RM.getyPos());
        double cbEME = Math.abs(RE.getxPos() - RM.getxPos());

        double RERM = (Math.hypot(acEME, cbEME));

        double acMN = Math.abs(RM.getyPos() - N.getyPos());
        double cbMN = Math.abs(RM.getxPos() - N.getxPos());

        double RMN = (Math.hypot(acMN, cbMN));

        double acMM = Math.abs(RM.getyPos() - LM.getyPos());
        double cbMM = Math.abs(RM.getxPos() - LM.getxPos());

        double RMLM = (Math.hypot(acMM, cbMM));

        double acMNL = Math.abs(N.getyPos() - LM.getyPos());
        double cbMNL = Math.abs(N.getxPos() - LM.getxPos());

        double LMN = (Math.hypot(acMNL, cbMNL));


        double s1 = (LELe + LeN + LEN)/ 2;
        double s2 = (LeRe + ReN + LeN)/ 2;
        double s3 = (ReRM + RERM + ReRE)/ 2;
        double s4 = (ReRM + RMN + ReN)/ 2;
        double s5 = (RMLM + RMN + LMN)/ 2;
        double s6 = (LELM + LMN + LEN)/ 2;

        double A1 = Math.sqrt(s1 * (s1 - LELe) * (s1 - LeN) * (s1 - LEN));
        double A2 = Math.sqrt(s2 * (s2 - LeRe) * (s2 - ReN) * (s2 - LeN));
        double A3 = Math.sqrt(s3 * (s3 - ReRM) * (s3 - RERM) * (s3 - ReRE));
        double A4 = Math.sqrt(s4 * (s4 - ReRM) * (s4 - RMN) * (s4 - ReN));
        double A5 = Math.sqrt(s5 * (s5 - RMLM) * (s5 - RMN) * (s5 - LMN));
        double A6 = Math.sqrt(s6 * (s6 - LELM) * (s6 - LMN) * (s6 - LEN));

        double a1 = Math.acos(((LELe * LELe) + (LEN * LEN) - (LeN * LeN))/(2 * LELe * LEN));
        double a2 = Math.acos(((LELe * LELe) + (LeN * LeN) - (LEN * LEN))/(2 * LELe * LeN));
        double a3 = Math.acos(((LeN * LeN) + (LEN * LEN) - (LELe * LELe))/(2 * LeN * LEN));
        double a4 = Math.acos(((LeRe * LeRe) + (LeN * LeN) - (ReN * ReN))/(2 * LeRe * LeN));
        double a5 = Math.acos(((LeRe * LeRe) + (ReN * ReN) - (LeN * LeN))/(2 * LeRe * ReN));
        double a6 = Math.acos(((LeN * LeN) + (ReN * ReN) - (LeRe * LeRe))/(2 * LeN * ReN));
        double a7 = Math.acos(((ReN * ReN) + (ReRM * ReRM) - (RMN * RMN))/(2 * ReN * ReRM));
        double a8 = Math.acos(((ReN * ReN) + (RMN * RMN) - (ReRM * ReRM))/(2 * ReN * RMN));
        double a9 = Math.acos(((RMN * RMN) + (ReRM * ReRM) - (ReN * ReN))/(2 * RMN * ReRM));
        double a10 = Math.acos(((ReRE * ReRE) + (ReRM * ReRM) - (RERM * RERM))/(2 * ReRE * ReRM));
        double a11 = Math.acos(((ReRE * ReRE) + (RERM * RERM) - (ReRM * ReRM))/(2 * ReRE * RERM));
        double a12 = Math.acos(((ReRM * ReRM) + (RERM * RERM) - (ReRE * ReRE))/(2 * ReRM * RERM));
        double a13 = Math.acos(((LEN * LEN) + (LELM * LELM) - (LMN * LMN))/(2 * LEN * LELM));
        double a14 = Math.acos(((LEN * LEN) + (LMN * LMN) - (LELM * LELM))/(2 * LEN * LMN));
        double a15 = Math.acos(((LELM * LELM) + (LMN * LMN) - (LEN * LEN))/(2 * LELM * LMN));
        double a16 = Math.acos(((LMN * LMN) + (RMN * RMN) - (RMLM * RMLM))/(2* LMN * RMN));
        double a17 = Math.acos(((LMN * LMN) + (RMLM * RMLM) - (RMN * RMN))/(2* LMN * RMLM));
        double a18 = Math.acos(((RMN * RMN) + (RMLM * RMLM) - (LMN * LMN))/(2* RMN * RMLM));

        return new double[][] {{A1, A2, A3, A4, A5, A6}, {a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16, a17, a18}};
    }

    private Bitmap getBitmapFromPathForImageView(String mCurrentPhotoPath, ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        Bitmap rotatedBitmap = bitmap;

        // rotate bitmap if needed
        try {
            ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotatedBitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotatedBitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotatedBitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return rotatedBitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
