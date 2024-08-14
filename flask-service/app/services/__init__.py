from app.services.coral_identification_service import CoralIdentificationService


def initialize_services():
    coral_identification_service = CoralIdentificationService()

    return {
        "coral_identification_service": coral_identification_service,
    }
