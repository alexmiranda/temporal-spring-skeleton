package io.github.alexmiranda.samples.temporal_poc.messages;

import lombok.Data;

@Data
public class EnrichAndVerifyRequestIn {
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
    private boolean completed;
}
