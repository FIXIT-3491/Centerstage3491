package org.firstinspires.ftc.teamcode.Test;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.CaptainHook.CH;
import org.firstinspires.ftc.teamcode.CaptainHook.Constants.CS;
import org.firstinspires.ftc.teamcode.CaptainHook.SparkFunOTOSConfig;
@Disabled
    @TeleOp(name = "TestOTOS", group = "Linear OpMode")
    public class TestOTOS extends LinearOpMode {
        // Create an instance of the sensor
        SparkFunOTOSConfig myOtos;
        private CH ch = null;


        @Override
        public void runOpMode() throws InterruptedException {
            // Get a reference to the sensor
            myOtos = hardwareMap.get(SparkFunOTOSConfig.class, "sensor_otos");
            ch = new CH(hardwareMap, this);

            // All the configuration for the OTOS is done in this helper method, check it out!
            configureOtos();

            // Wait for the start button to be pressed
            waitForStart();
            // Loop until the OpMode ends
            while (opModeIsActive()) {
                ch.wrist.setPosition(CS.WRIST_UP);
                // Get the latest position, which includes the x and y coordinates, plus the
                // heading angle
                SparkFunOTOSConfig.Pose2D pos = myOtos.getPosition();

                // Reset the tracking if the user requests it
                if (gamepad1.y) {
                    myOtos.resetTracking();
                }

                // Re-calibrate the IMU if the user requests it
                if (gamepad1.x) {
                    myOtos.calibrateImu();
                }


                double max;
                double axial = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
                double lateral = gamepad1.left_stick_x;
                double yaw = gamepad1.right_stick_x;
                double leftFrontPower = axial + lateral + yaw;
                double rightFrontPower = axial - lateral - yaw;
                double leftBackPower = axial - lateral + yaw;
                double rightBackPower = axial + lateral - yaw;
                max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
                max = Math.max(max, Math.abs(leftBackPower));
                max = Math.max(max, Math.abs(rightBackPower));
                if (max > 1.0) {
                    leftFrontPower /= max;
                    rightFrontPower /= max;
                    leftBackPower /= max;
                    rightBackPower /= max;
                }
                if (gamepad1.left_bumper || gamepad1.right_bumper) { // slow power
                    ch.frontLDrive.setPower(0.35 * leftFrontPower);
                    ch.frontRDrive.setPower(0.35 * rightFrontPower);
                    ch.backLDrive.setPower(0.35 * leftBackPower);
                    ch.backRDrive.setPower(0.35 * rightBackPower);
                } else { // full power
                    ch.frontLDrive.setPower(leftFrontPower);
                    ch.frontRDrive.setPower(rightFrontPower);
                    ch.backLDrive.setPower(leftBackPower);

                    ch.backRDrive.setPower(rightBackPower);
                }


                // Inform user of available controls
                telemetry.addLine("Press Y (triangle) on Gamepad to reset tracking");
                telemetry.addLine("Press X (square) on Gamepad to calibrate the IMU");
                telemetry.addLine();

                // Log the position to the telemetry
                telemetry.addData("X coordinate", pos.x);
                telemetry.addData("Y coordinate", pos.y);
                telemetry.addData("Heading angle", pos.h);

                // Update the telemetry on the driver station
                telemetry.update();
            }
        }

        private void configureOtos() {
            telemetry.addLine("Configuring OTOS...");
            telemetry.update();

            // Set the desired units for linear and angular measurements. Can be either
            // meters or inches for linear, and radians or degrees for angular. If not
            // set, the default is inches and degrees. Note that this setting is not
            // stored in the sensor, it's part of the library, so you need to set at the
            // start of all your programs.
            // myOtos.setLinearUnit(SparkFunOTOS.LinearUnit.METERS);
            myOtos.setLinearUnit(SparkFunOTOSConfig.LinearUnit.INCHES);
            // myOtos.setAngularUnit(SparkFunOTOS.AngularUnit.RADIANS);
            myOtos.setAngularUnit(SparkFunOTOSConfig.AngularUnit.DEGREES);

            // Assuming you've mounted your sensor to a robot and it's not centered,
            // you can specify the offset for the sensor relative to the center of the
            // robot. The units default to inches and degrees, but if you want to use
            // different units, specify them before setting the offset! Note that as of
            // firmware version 1.0, these values will be lost after a power cycle, so
            // you will need to set them each time you power up the sensor. For example, if
            // the sensor is mounted 5 inches to the left (negative X) and 10 inches
            // forward (positive Y) of the center of the robot, and mounted 90 degrees
            // clockwise (negative rotation) from the robot's orientation, the offset
            // would be {-5, 10, -90}. These can be any value, even the angle can be
            // tweaked slightly to compensate for imperfect mounting (eg. 1.3 degrees).
            SparkFunOTOSConfig.Pose2D offset = new SparkFunOTOSConfig.Pose2D(3, -1, 90);
            myOtos.setOffset(offset);

            // Here we can set the linear and angular scalars, which can compensate for
            // scaling issues with the sensor measurements. Note that as of firmware
            // version 1.0, these values will be lost after a power cycle, so you will
            // need to set them each time you power up the sensor. They can be any value
            // from 0.872 to 1.127 in increments of 0.001 (0.1%). It is recommended to
            // first set both scalars to 1.0, then calibrate the angular scalar, then
            // the linear scalar. To calibrate the angular scalar, spin the robot by
            // multiple rotations (eg. 10) to get a precise error, then set the scalar
            // to the inverse of the error. Remember that the angle wraps from -180 to
            // 180 degrees, so for example, if after 10 rotations counterclockwise
            // (positive rotation), the sensor reports -15 degrees, the required scalar
            // would be 3600/3585 = 1.004. To calibrate the linear scalar, move the
            // robot a known distance and measure the error; do this multiple times at
            // multiple speeds to get an average, then set the linear scalar to the
            // inverse of the error. For example, if you move the robot 100 inches and
            // the sensor reports 103 inches, set the linear scalar to 100/103 = 0.971
            myOtos.setLinearScalar(1.071);
            myOtos.setAngularScalar(1.0);

            // The IMU on the OTOS includes a gyroscope and accelerometer, which could
            // have an offset. Note that as of firmware version 1.0, the calibration
            // will be lost after a power cycle; the OTOS performs a quick calibration
            // when it powers up, but it is recommended to perform a more thorough
            // calibration at the start of all your programs. Note that the sensor must
            // be completely stationary and flat during calibration! When calling
            // calibrateImu(), you can specify the number of samples to take and whether
            // to wait until the calibration is complete. If no parameters are provided,
            // it will take 255 samples and wait until done; each sample takes about
            // 2.4ms, so about 612ms total
            myOtos.calibrateImu();

            // Reset the tracking algorithm - this resets the position to the origin,
            // but can also be used to recover from some rare tracking errors
            myOtos.resetTracking();

            // After resetting the tracking, the OTOS will report that the robot is at
            // the origin. If your robot does not start at the origin, or you have
            // another source of location information (eg. vision odometry), you can set
            // the OTOS location to match and it will continue to track from there.
            SparkFunOTOSConfig.Pose2D currentPosition = new SparkFunOTOSConfig.Pose2D(0, 0, 0);
            myOtos.setPosition(currentPosition);

            // Get the hardware and firmware version
            SparkFunOTOSConfig.Version hwVersion = new SparkFunOTOSConfig.Version();
            SparkFunOTOSConfig.Version fwVersion = new SparkFunOTOSConfig.Version();
            myOtos.getVersionInfo(hwVersion, fwVersion);

            telemetry.addLine("OTOS configured! Press start to get position data!");
            telemetry.addLine();
            telemetry.addLine(String.format("OTOS Hardware Version: v%d.%d", hwVersion.major, hwVersion.minor));
            telemetry.addLine(String.format("OTOS Firmware Version: v%d.%d", fwVersion.major, fwVersion.minor));
            telemetry.update();
        }

    }



//            SparkFunOTOSConfig.Pose2D pos = myOtos.getPosition();