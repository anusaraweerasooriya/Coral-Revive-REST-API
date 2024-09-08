from app.services.schedule_prioritization_service import SchedulePrioritizationService
from app.services.weather_prediction_service import WeatherPredictionService
from app.services.coral_identification_service import CoralIdentificationService
from app.services.comment_classification_service import CommentClassificationService
from app.services.training_service import TrainingService
from app.services.kgcn_model_service import KGCNModelService
from app.services.post_classification_service import PostClassificationService

def initialize_services():
    coral_identification_service = CoralIdentificationService()
    weather_prediction_service = WeatherPredictionService()
    schedule_prioritization_service = SchedulePrioritizationService()
    comment_classification_service = CommentClassificationService()
    training_service = TrainingService()
    recommendation_service = KGCNModelService()
    text_classification_service = PostClassificationService()

    return {
        "coral_identification_service": coral_identification_service,
        "weather_prediction_service": weather_prediction_service,
        "schedule_prioritization_service": schedule_prioritization_service,
        "comment_classification_service": comment_classification_service,
        "training_service":training_service,
        "recommendation_service":recommendation_service,
        "text_classification_service":text_classification_service  
    }


