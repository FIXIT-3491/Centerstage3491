package org.firstinspires.ftc.teamcode.Test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.CaptainHook.CH;
import org.firstinspires.ftc.teamcode.CaptainHook.VP;

@Autonomous(name="AprilTagTest", group="Linear OpMode")

public class AprilTagTest extends LinearOpMode {
    public CH ch = null;
    private VP vp = null;
    private ElapsedTime stepTimer = new ElapsedTime();
    private String Location;

    @Override
    public void runOpMode() {
        telemetry.update();

        ch = new CH(hardwareMap, this);
        vp = new VP(hardwareMap, this);

        vp.initCompVision();

        ch.rightPincer.setPosition(0.5);

        telemetry.addData("Status", "initialized ");
        telemetry.update();

        waitForStart();

        stepTimer.reset();
        if (opModeIsActive())
        {
            vp.DESIRED_TAG_ID = 6;
            ch.moveAprilTag(vp);

        } // if active
    } // run op mode
    private void TelemetryStep(String step) {
        telemetry.addData("Step", step);
        telemetry.addData("prop location", Location);
        telemetry.update();
    }
    private void YellowPixel(){
        vp.visionPortal.setActiveCamera(vp.webcam1);
        stepTimer.reset();

        TelemetryStep("Move april tag");
        ch.moveAprilTag(vp);
        TelemetryStep("Move to backdrop");
        ch.moveRobot(-0.4,0,0);
        sleep(1200);
        ch.moveRobot(0,0,0);
        TelemetryStep("Move arm up");
        ch.armMove(2100);
        TelemetryStep("Drop on backdrop ");
        ch.rightPincer.setPosition(0.85);
        sleep(400);
        TelemetryStep("Lower Arm");
        ch.armMove(0);
        sleep(500);
        TelemetryStep("Close Pincer");
        ch.rightPincer.setPosition(0.55);
        TelemetryStep("Drive off backdrop");
        ch.EncoderMove(400);
        TelemetryStep("turn to 0");
        ch.imuTurn(179);
        TelemetryStep("move to park");
        ch.EncoderMove(900);
        TelemetryStep("park ");
        ch.moveRobot(0,0.5,0);
        sleep(1000);
        ch.moveRobot(0,0,0);
    }
} //linear op mode