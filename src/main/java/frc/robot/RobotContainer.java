// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
import edu.wpi.first.wpilibj2.command.RepeatCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.autos.*;
import frc.robot.commands.*;
import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  /* Controllers */
  private final Joystick driver = new Joystick(0);

  /* Drive Controls */
  private final int translationAxis = XboxController.Axis.kLeftY.value;
  private final int strafeAxis = XboxController.Axis.kLeftX.value;
  private final int rotationAxis = XboxController.Axis.kRightX.value;
  private final POVButton dPad_Right = new POVButton(driver, 90, 0);
  private final POVButton dPad_Top = new POVButton(driver, 0, 0);
  private final POVButton dPad_Left = new POVButton(driver, 270, 0);
  
  /* Driver Buttons */
  private final JoystickButton robotCentric = new JoystickButton(driver, XboxController.Button.kStart.value);
  private final JoystickButton fastSpeed = new JoystickButton(driver, XboxController.Button.kLeftBumper.value);

  private final JoystickButton slowSpeed = new JoystickButton(driver, XboxController.Button.kRightBumper.value);
  private final JoystickButton m_intakeIn = new JoystickButton(driver, XboxController.Button.kX.value);
  private final JoystickButton m_intakeOut = new JoystickButton(driver, XboxController.Button.kY.value);
  private final JoystickButton m_push = new JoystickButton(driver, XboxController.Button.kA.value);
  private final JoystickButton m_pull = new JoystickButton(driver, XboxController.Button.kB.value);
  private final JoystickButton m_rotateLiftPosition = new JoystickButton(driver, XboxController.Button.kBack.value);
  
  

  /* Subsystems */
  public final Swerve s_Swerve = new Swerve();
  public final Intaker s_Intaker = new Intaker();
  public final Lifter s_Lifter = new Lifter();


  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    s_Swerve.setDefaultCommand(
        // Command that's continuously run to update the swerve state
        new TeleopSwerve(
            // The Swerve subsystem
            s_Swerve,
            // getRawAxis() returns a value for the controller axis from -1 to 1
            () -> driver.getRawAxis(translationAxis),
            () -> driver.getRawAxis(strafeAxis),
            () -> driver.getRawAxis(rotationAxis),
            // robotCentric and slowSpeed are both buttons on the joystick
            // The robotCentric button, when held down, enables axis behavior relative to the field (and requires a working gyroscope).  The default
            // is for movements to apply relative to the robot.
            () -> robotCentric.getAsBoolean(),
            // slowSpeed button, when held, causes translation and rotation to be performed at a slower speed
            () -> slowSpeed.getAsBoolean()));

    // Configure the button bindings
    configureButtonBindings();
    //s_Intaker.setDefaultCommand(new RunCommand(s_Intaker::stop, s_Intaker));

    //s_Lifter.setDefaultCommand(new RunCommand(s_Lifter::stop, s_Lifter));

  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing
   * it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    /* Driver Buttons */

 
   // m_intakeIn.whileTrue(new RunCommand(() -> s_Intaker.pull()));
    m_intakeIn.whileTrue(new StartEndCommand(() -> s_Intaker.pull(), () -> s_Intaker.stop()));
    m_intakeOut.whileTrue(new StartEndCommand(() -> s_Intaker.push(), () -> s_Intaker.stop()));

    // m_pull.whileTrue(new RunCommand(() -> s_Lifter.pull()));
    // m_push.whileTrue(new RunCommand(() -> s_Lifter.push()));
    // m_pull.whileTrue(new RepeatCommand(new RunCommand(() -> s_Lifter.pull())));
    // m_push.whileTrue(new RepeatCommand(new RunCommand(() -> s_Lifter.push())));
    m_pull.whileTrue(new StartEndCommand(() -> s_Lifter.pull(), () -> s_Lifter.stop()));
    m_push.whileTrue(new StartEndCommand(() -> s_Lifter.push(), () -> s_Lifter.stop()));

    //TODO: Test position method for the lifter
    dPad_Left.onTrue(new InstantCommand(() -> s_Lifter.setToPosition(4)));
    dPad_Top.onTrue(new InstantCommand(() -> s_Lifter.setToPosition(6)));
    dPad_Right.onTrue(new InstantCommand(() -> s_Lifter.setToPosition(8)));
    
    
    


  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An ExampleCommand will run in autonomous
    return new exampleAuto(this);
  }

  public void resetToAbsoluteNorth() {
    s_Swerve.resetToAbsoluteNorth();
  }

  /**
   * This is run constantly as soon as the robot is plugged in.
   */
  public void periodic() {
    s_Lifter.checkLimits();
    s_Intaker.periodic();
  }
  
}
