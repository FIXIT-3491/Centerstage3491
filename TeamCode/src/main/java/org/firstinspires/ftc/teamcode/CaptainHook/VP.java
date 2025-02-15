package org.firstinspires.ftc.teamcode.CaptainHook;


import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;


import java.util.List;
import java.util.concurrent.TimeUnit;

public class VP {
    public VisionPortal visionPortal;
    public AprilTagProcessor aprilTag;
    public WebcamName webcam1, webcam2;
    public int DESIRED_TAG_ID = 0;

    public static final String TFOD_MODEL_ASSET = "rookDetection.tflite";
    public static final String[] LABELS = {"rook"};
    public TfodProcessor tfod;

    private LinearOpMode opMode_ref = null;



    public VP(HardwareMap hardwareMap, LinearOpMode op) {
        opMode_ref = op;

        webcam2 = hardwareMap.get(WebcamName.class, "Webcam 2");
    }

    public void initCompVision() {


        // Create the AprilTag processor by using a builder.
        aprilTag = new AprilTagProcessor.Builder()
          //      .setLensIntrinsics(1439.41944052, 1439.41944052, 970.51421863, 537.612825157)  //logitech c920
                .build();
        // Adjust Image Decimation to trade-off detection-range for detection-rate.
        // eg: Some typical detection data using a Logitech C920 WebCam
        // Decimation = 1 ..  Detect 2" Tag from 10 feet away at 10 Frames per second
        // Decimation = 2 ..  Detect 2" Tag from 6  feet away at 22 Frames per second
        // Decimation = 3 ..  Detect 2" Tag from 4  feet away at 30 Frames Per Second
        // Decimation = 3 ..  Detect 5" Tag from 10 feet away at 30 Frames Per Second
        // Note: Decimation can be changed on-the-fly to adapt during a match.
        aprilTag.setDecimation(2);

        tfod = new TfodProcessor.Builder()
                .setModelAssetName(TFOD_MODEL_ASSET)
                .setModelLabels(LABELS)
                .build();
        tfod.setMinResultConfidence(0.80f);

        visionPortal = new VisionPortal.Builder()
                .setCamera(webcam2)
                .setCameraResolution(new Size(640, 480))
                .addProcessor(aprilTag)
                .addProcessor(tfod)
                .build();

    }

    public void setManualExposure(int exposureMS) {

//        if(visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING){
//            while (!opMode_ref.isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)){
//                opMode_ref.sleep(20);
//            }
//        }
        ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
        if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
            exposureControl.setMode(ExposureControl.Mode.Manual);
            opMode_ref.sleep(50);
        }
        exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
//        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
//        gainControl.setGain(gain);
        opMode_ref.sleep(20);
        }

    public String TensorDetect(){
        boolean cupFound = false;
        ElapsedTime TensorTimer = new ElapsedTime();
        TensorTimer.reset();
        String PropLocation = "not detected1";

        while (TensorTimer.milliseconds() < 3000 && !cupFound && opMode_ref.opModeIsActive()) {
            List<Recognition> currentRecognitions = tfod.getRecognitions();
            opMode_ref.telemetry.addData("Time", TensorTimer.milliseconds());
            opMode_ref.telemetry.update();
            // Step through the list of recognitions and display info for each one.
            for (Recognition recognition : currentRecognitions) {
                cupFound = true;
                double x = (recognition.getLeft() + recognition.getRight()) / 2;
                if (x < 200) {
                    PropLocation = "left";
                    break;
                } else if (x < 430) {
                    PropLocation = "middle";
                    break;
                } else {
                    PropLocation = "right";
                    break;
                }
            }
        }
        return PropLocation;
    }
}