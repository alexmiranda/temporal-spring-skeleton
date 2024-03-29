package io.github.alexmiranda.samples.temporal_poc.onboarding;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "onboarding_case")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private LocalDate dateOfBirth;

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

    @Setter
    @Column(name = "name_screening_required")
    private Boolean nameScreeningRequired;

    @Setter
    @Column(name = "adverse_media_screening_required")
    private Boolean adverseMediaScreeningRequired;

    @Setter
    @Column(name = "credit_bureau_check_required")
    private Boolean creditBureauCheckRequired;

    @Setter
    @Column(name = "card_type")
    private CardType cardType;

    @Column(name = "card_fees")
    private Float cardFees;

    @Setter
    @Column(name = "use_paper_communication")
    private Boolean usePaperCommunication;

    @Column(name = "communication_fees")
    private Float communicationFees;

    @Setter
    @Column(name = "fee_waiver_requested")
    private Boolean feeWaiverRequested;

    @Column(name = "yearly_fees")
    private Float yearlyFees;

    @Setter
    @Column(name = "rejection_reason")
    private String rejectionReason;

    public OnboardingCase(UUID workflowId, String branchName, String requestType) {
        this.workflowId = workflowId;
        this.branchName = branchName;
        this.requestType = requestType;
    }

    public void recalculateFees() {
        if (this.cardType != null) {
            this.cardFees = switch (this.cardType) {
                case Master -> 8F;
                case Visa -> 6F;
            };
        }
        if (Objects.equals(this.usePaperCommunication, true)) {
            this.communicationFees = 3F;
        } else {
            this.communicationFees = 0F;
        }
        if (Objects.equals(this.feeWaiverRequested, true)) {
            this.yearlyFees = 0F;
        }
        var age = this.age();
        if (age == null) {
            this.yearlyFees = null;
        } else if (age < 25) {
            this.yearlyFees = 2.5F;
        } else if (age < 60) {
            this.yearlyFees = 5F;
        } else {
            this.yearlyFees = 3F;
        }
    }

    private Integer age() {
        var today = LocalDate.now();
        if (this.dateOfBirth == null) {
            return null;
        }
        return Period.between(this.dateOfBirth, today).getYears();
    }

    public boolean isScreeningRequired() {
        return Objects.equals(this.nameScreeningRequired, true) ||
            Objects.equals(this.adverseMediaScreeningRequired, true) ||
            Objects.equals(this.creditBureauCheckRequired, true);
    }
}
