package io.github.alexmiranda.samples.temporal_poc.messages;

import lombok.Data;

@Data
public class OnboardingRequestIn {
    private String id;
    private String firstName;
    private String lastName;
    private String salutation;
    private String dateOfBirth;
    private String bsn;
    private String street;
    private String houseNumber;
    private String city;
    private String province;
    private String country;
    private String mobilePhone;
    private String homePhone;
    private String whatsApp;
    private String email;
    private Boolean nameScreeningRequired;
    private Boolean adverseMediaScreeningRequired;
    private Boolean creditBureauCheckRequired;
    private String cardType;
    private Boolean usePaperCommunication;
    private Boolean feeWaiverRequested;
    private String rejectionReason;
    private String action;
    private boolean completed;
}
