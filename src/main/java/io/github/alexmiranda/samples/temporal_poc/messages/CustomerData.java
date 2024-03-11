package io.github.alexmiranda.samples.temporal_poc.messages;

import lombok.Data;

@Data
public class CustomerData {
    private String firstName;
    private String lastName;
    private String bsn;
    private String dateOfBirth;
    private String email;
    private boolean nameScreeningRequired;
    private boolean adverseMediaScreeningRequired;
    private boolean creditBureauCheckRequired;
}
