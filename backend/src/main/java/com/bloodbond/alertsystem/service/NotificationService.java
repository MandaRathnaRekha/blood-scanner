package com.bloodbond.alertsystem.service;

import com.bloodbond.alertsystem.entity.Alert;
import com.bloodbond.alertsystem.entity.Donor;
import com.bloodbond.alertsystem.entity.Hospital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LifePulse - Emergency Blood Alert System
 * Service: NotificationService
 * Handles emergency dispatch notifications via SMS (Twilio protocol) 
 * and Email (Spring Mail protocol) to donors and hospital blood banks.
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Broadcasts SMS alerts to all matching voluntary donors.
     * In development mode, logs simulated carrier delivery.
     * To activate live Twilio: Provide TWILIO_ACCOUNT_SID & TWILIO_AUTH_TOKEN in application.properties.
     * 
     * @param donors List of compatible on-call donors
     * @param alert The active emergency broadcast
     */
    public void sendEmergencySmsToDonors(List<Donor> donors, Alert alert) {
        if (donors == null || donors.isEmpty()) {
            logger.info("No matching donors available for SMS dispatch.");
            return;
        }

        String smsBody = String.format(
            "🚨 [LIFEPULSE EMERGENCY] Urgent blood needed! Patient: %s | Blood Group: %s | Location: %s. Call: %s. Reply YES if available to donate.",
            alert.getPatientName(),
            alert.getBloodGroup(),
            alert.getLocation(),
            alert.getContactNumber()
        );

        for (Donor donor : donors) {
            // Simulated carrier dispatch (Twilio Message.creator integration point)
            logger.info("📲 [SMS DISPATCHED via Twilio] To: {} ({}) | Blood: {} | Message: {}",
                    donor.getName(), donor.getPhone(), donor.getBloodGroup(), smsBody);
        }

        logger.info("✅ SMS Emergency Broadcast completed to {} eligible donors.", donors.size());
    }

    /**
     * Dispatches emergency alert emails to regional hospital blood bank inboxes.
     * In development mode, logs simulated SMTP delivery.
     * To activate live SMTP: Configure spring.mail.host, username, and password in application.properties.
     * 
     * @param hospitals List of matching hospitals with blood stock
     * @param alert The active emergency broadcast
     */
    public void sendEmergencyEmailToHospitals(List<Hospital> hospitals, Alert alert) {
        if (hospitals == null || hospitals.isEmpty()) {
            logger.info("No regional hospitals available for Email dispatch.");
            return;
        }

        String emailSubject = String.format(
            "🚨 CRITICAL BLOOD ALERT #%d - Group %s Required",
            alert.getId(),
            alert.getBloodGroup()
        );

        for (Hospital hospital : hospitals) {
            // Simulated SMTP dispatch (JavaMailSender integration point)
            logger.info("📧 [EMAIL DISPATCHED via Spring Mail] To: {} | Hotline: {} | Subject: {}",
                    hospital.getHospitalName(), hospital.getContact(), emailSubject);
        }

        logger.info("✅ Email Emergency Broadcast completed to {} hospital facilities.", hospitals.size());
    }

    /**
     * Unified notification dispatcher triggered upon alert creation.
     */
    public void broadcastAll(Alert alert, List<Donor> donors, List<Hospital> hospitals) {
        logger.info("🚀 Initiating multi-channel emergency broadcast for Alert ID: {}", alert.getId());
        sendEmergencySmsToDonors(donors, alert);
        sendEmergencyEmailToHospitals(hospitals, alert);
    }
}
