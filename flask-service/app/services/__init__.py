from app.services.coral_identification_service import CoralIdentificationService
from app.services.comment_classification_service import CommentClassificationService
from app.services.training_service import TrainingService
from app.services.kgcn_model_service import KGCNModelService

def initialize_services():
    coral_identification_service = CoralIdentificationService()
    comment_classification_service = CommentClassificationService()
    training_service = TrainingService()
    recommendation_service = KGCNModelService()

    return {
        "coral_identification_service": coral_identification_service,
        "comment_classification_service": comment_classification_service,
        "training_service":training_service,
        "recommendation_service":recommendation_service
    }