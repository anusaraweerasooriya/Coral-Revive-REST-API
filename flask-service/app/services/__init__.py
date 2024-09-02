from app.services.schedule_prioritization_service import SchedulePrioritizationService
from app.services.weather_prediction_service import WeatherPredictionService
from app.services.coral_identification_service import CoralIdentificationService

def initialize_services():
    coral_identification_service = CoralIdentificationService()
    weather_prediction_service = WeatherPredictionService()
    schedule_prioritization_service = SchedulePrioritizationService()

    return {
        "coral_identification_service": coral_identification_service,
        "weather_prediction_service": weather_prediction_service,
        "schedule_prioritization_service": schedule_prioritization_service,
    }
