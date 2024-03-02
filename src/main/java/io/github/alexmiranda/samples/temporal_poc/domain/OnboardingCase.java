package io.github.alexmiranda.samples.temporal_poc.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "onboarding_case")
@Getter
public class OnboardingCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "workflow_id", nullable = false)
    UUID workflowId;

    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "request_type", nullable = false)
    private String requestType;

    @Setter
    @Column(name = "first_name")
    private String firstName;

    @Setter
    @Column(name = "last_name")
    private String lastName;

    @Setter
    @Column(name = "salutation")
    private String salutation;

    @Setter
    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Setter
    @Column(name = "bsn")
    private String bsn;

    @Setter
    @Column(name = "street")
    private String street;

    @Setter
    @Column(name = "house_number")
    private String houseNumber;

    @Setter
    @Column(name = "city")
    private String city;

    @Setter
    @Column(name = "province")
    private String province;

    @Setter
    @Column(name = "country")
    private String country;

    @Setter
    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Setter
    @Column(name = "home_phone")
    private String homePhone;

    @Setter
    @Column(name = "whatsapp")
    private String whatsApp;

    @Setter
    @Column(name = "email")
    private String email;

    protected OnboardingCase() {
    }

    public OnboardingCase(UUID workflowId, String branchName, String requestType) {
        this.workflowId = workflowId;
        this.branchName = branchName;
        this.requestType = requestType;
    }
}
