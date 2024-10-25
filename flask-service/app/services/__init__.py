from app.services.schedule_prioritization_service import SchedulePrioritizationService
from app.services.weather_prediction_service import WeatherPredictionService
from app.services.coral_identification_service import CoralIdentificationService
from app.services.coral_growth_analysis_service import CoralGrowthAnalysisService
from app.services.coral_polyp_count_service import CoralPolypCountService
from app.services.training_service import TrainingService
from app.services.kgcn_model_service import KGCNModelService

def initialize_services():
    coral_identification_service = CoralIdentificationService()
    coral_growth_analysis_service = CoralGrowthAnalysisService()
    weather_prediction_service = WeatherPredictionService()
    schedule_prioritization_service = SchedulePrioritizationService()
    coral_polyp_count_service = CoralPolypCountService()
    training_service = TrainingService()
    recommendation_service = KGCNModelService()

    return {
        "coral_identification_service": coral_identification_service,
        "coral_growth_analysis_service": coral_growth_analysis_service,
        "weather_prediction_service": weather_prediction_service,
        "schedule_prioritization_service": schedule_prioritization_service,
        "coral_polyp_count_service": coral_polyp_count_service,
        "training_service":training_service,
        "recommendation_service":recommendation_service,
    }


