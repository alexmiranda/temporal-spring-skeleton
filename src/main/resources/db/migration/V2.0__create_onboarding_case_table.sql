CREATE TABLE "onboarding_case" (
  "id" BIGINT GENERATED ALWAYS AS IDENTITY(START WITH 1 INCREMENT BY 1),
  "workflow_id" RAW(16) NOT NULL,
  "branch_name" VARCHAR(20) NOT NULL,
  "request_type" VARCHAR(20) NOT NULL,
  PRIMARY KEY ("id")
);
