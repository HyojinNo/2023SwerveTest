package frc.robot.subsystems;


//import edu.wpi.first.wpilibj.XboxController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;

import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
//import com.revrobotics.RelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.XboxController;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Encoder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.Constants;

public class Lifter extends SubsystemBase {
    /*Config motors and encoders */
    //private final CANSparkMax m_intake = new CANSparkMax(Constants.IntakeConstants.Mod4.intakeMotorID,MotorType.kBrushless);
    //private Encoder intakeEncoder = new Encoder(Constants.IntakeConstants.Mod4.intakeMotorID1, Constants.IntakeConstants.Mod4.intakeMotorID2, Constants.IntakeConstants.Mod4.kEncoderReversed);
    private Rotation2d angleOffset;
    private final CANSparkMax lifterMotor = new CANSparkMax(Constants.LiftConstants.Mod5.kLiftMotorID, MotorType.kBrushless);
    private final RelativeEncoder lifterEncoder;
    private final SparkMaxPIDController lifterController;
    private final Encoder liftEncoder = new Encoder(Constants.LiftConstants.Mod5.kEncoderChannelA, Constants.LiftConstants.Mod5.kEncoderChannelB);
    private final DigitalInput bottomLimitSwitch = new DigitalInput(0);
    private final DigitalInput topLimitSwitch = new DigitalInput(1);
    private int travelDirection = 0;
   
   


    public Lifter (){
        lifterEncoder = lifterMotor.getEncoder();
        lifterController = lifterMotor.getPIDController();
        
       
        liftEncoder.setDistancePerPulse(Constants.LiftConstants.Mod5.kEncoderDistancePerPulse);
        lifterEncoder.setVelocityConversionFactor(Constants.LiftConstants.Mod5.kLifterMotorVelocityFactor);
        lifterEncoder.setPositionConversionFactor(Constants.LiftConstants.Mod5.kLifterMotorPositionFactor);
        lifterController.setP(Constants.LiftConstants.Mod5.angleKP);
        lifterController.setI(Constants.LiftConstants.Mod5.angleKI);
        lifterController.setD(Constants.LiftConstants.Mod5.angleKD);
        lifterController.setFF(Constants.LiftConstants.Mod5.angleKFF);
        lifterController.setFeedbackDevice(lifterEncoder);
        lifterEncoder.setPosition(lifterEncoder.getPosition() - lifterEncoder.getPosition());
    }

   



    // turn on the intake motor to pull in objects.
    public void push() {
        
        SmartDashboard.putString("Lifter Status", "PUSHING");
        SmartDashboard.putNumber("Lifter P Value", lifterMotor.getPIDController().getP());
        SmartDashboard.putNumber("Lifter I Value", lifterMotor.getPIDController().getI());
        SmartDashboard.putNumber("Lifter D Value", lifterMotor.getPIDController().getD());
        lifterMotor.set(0.25);
        travelDirection = 1;
    }

    // turn on the intake motor to push out objects.
    public void pull() {
        
        SmartDashboard.putString("Lifter Status", "PULLING");
        SmartDashboard.putNumber("Lifter P Value", lifterMotor.getPIDController().getP());
        SmartDashboard.putNumber("Lifter I Value", lifterMotor.getPIDController().getI());
        SmartDashboard.putNumber("Lifter D Value", lifterMotor.getPIDController().getD());
        lifterMotor.set(-0.25);
        travelDirection = -1;
    }

    // this is the default state of the intake motor - do not move.
    public void stop() {
        
        SmartDashboard.putString("Lifter Status", "STOPPED");
        SmartDashboard.putNumber("Lifter P Value", lifterMotor.getPIDController().getP());
        SmartDashboard.putNumber("Lifter I Value", lifterMotor.getPIDController().getI());
        SmartDashboard.putNumber("Lifter D Value", lifterMotor.getPIDController().getD());
        lifterMotor.set(0);
        travelDirection = 0;
    }

    

    public void setToPosition(double distance) {
        //Prevent rotating module if speed is less then 1%. Prevents jittering.
        lifterController.setReference(distance/Constants.LiftConstants.Mod5.kPulleyRatio, ControlType.kPosition);
    }

    public void checkLimits() {
        SmartDashboard.putBoolean("Top Limit Switch:", topLimitSwitch.get());
        SmartDashboard.putBoolean("Bottom Limit Switch: ", bottomLimitSwitch.get());
        if(travelDirection == 1 && topLimitSwitch.get()) {
            stop();
        } else if(travelDirection == -1 && bottomLimitSwitch.get()) {
            stop();
        }
        
    }

    /*
    public void setToPosition(JoystickButton button) {
        lifterEncoder.setPosition(0.0);
        if(button.equals(XboxController.Button.kA)) {
            lifterEncoder.setPosition(0.2);           
        } else if(button.equals(XboxController.Button.kB)) {
            lifterEncoder.setPosition(0.3);
        } else if(button.equals(XboxController.Button.kX)) {
            lifterEncoder.setPosition(0.4);
        }
    }
    */

}

