package org.firstinspires.ftc.teamcode.Test;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;


import org.firstinspires.ftc.teamcode.CaptainHook.Constants.CS;
import org.firstinspires.ftc.teamcode.CaptainHook.CH;
@Disabled
@Autonomous(name="ArmTest", group="Linear OpMode")

public class ArmTest extends LinearOpMode {
    public CH ch = null;

    @Override
    public void runOpMode() {

        ch = new CH(hardwareMap, this);
        ch.wrist.setPosition(CS.WRIST_UP);
        ch.rightPincer.setPosition(CS.C_RIGHT_CLOSE);
        waitForStart();

        while (opModeIsActive()){
ch.EncoderMove(300);


            telemetry.addData("shoulder position", ch.shoulder.getCurrentPosition());
            telemetry.addData("armExt position", ch.armExtender.getCurrentPosition());
            telemetry.addData("right pincer", ch.rightPincer.getPosition());
            telemetry.addData("left pincer", ch.leftPincer.getPosition());
            telemetry.addData("wrist", ch.wrist.getPosition());
            telemetry.update();

        }

    }
}
