from flask import Blueprint, request, jsonify
from app.services.resource_estimation_service import ReefBowlEstimationService
from app.services.resource_estimation_service import ManpowerEstimationService
from app.services.resource_estimation_service import NumberOfBoatsEstimationService
from app.services.resource_estimation_service import NumberOfDivingKitsEstimationService
from app.services.resource_estimation_service import NumberOfReefSegmentsEstimationService
from app.services.resource_estimation_service import AmountOfBoundingGlueEstimationService
from app.services.resource_estimation_service import TaskManpowerEstimationService
from app.services.resource_estimation_service import TaskSkillMatchingService
from app.services.resource_estimation_service import OxygenCapacityEstimationService

reef_bowl_estimation_bp = Blueprint('reef_bowl_estimation', __name__)
reef_bowl_estimation_service = ReefBowlEstimationService()

@reef_bowl_estimation_bp.route('/predict-reef-bowls', methods=['POST'])
def predict_reef_bowls():
    data = request.get_json()
    predictions = reef_bowl_estimation_service.predict(data)
    return jsonify({'predictions': predictions})


manpower_estimation_bp = Blueprint('manpower_estimation', __name__)
manpower_estimation_service = ManpowerEstimationService()

@manpower_estimation_bp.route('/predict-manpower', methods=['POST'])
def predict_manpower():
    data = request.get_json()
    predictions = manpower_estimation_service.predict(data)
    return jsonify({'predictions': predictions})


number_of_boats_estimation_bp = Blueprint('number_of_boats_estimation', __name__)
number_of_boats_estimation_service = NumberOfBoatsEstimationService()

@number_of_boats_estimation_bp.route('/predict-number-of-boats', methods=['POST'])
def predict_number_of_boats():
    data = request.get_json()
    predictions = number_of_boats_estimation_service.predict(data)
    return jsonify({'predictions': predictions})


number_of_diving_kits_estimation_bp = Blueprint('number_of_diving_kits_estimation', __name__)
number_of_diving_kits_estimation_service = NumberOfDivingKitsEstimationService()

@number_of_diving_kits_estimation_bp.route('/predict-number-of-diving-kits', methods=['POST'])
def predict_number_of_diving_kits():
    data = request.get_json()
    predictions = number_of_diving_kits_estimation_service.predict(data)
    return jsonify({'predictions': predictions})


number_of_reef_segments_estimation_bp = Blueprint('number_of_reef_segments_estimation', __name__)
number_of_reef_segments_estimation_service = NumberOfReefSegmentsEstimationService()

@number_of_reef_segments_estimation_bp.route('/predict-number-of-reef-segments', methods=['POST'])
def predict_number_of_reef_segments():
    data = request.get_json()
    predictions = number_of_reef_segments_estimation_service.predict(data)
    return jsonify({'predictions': predictions})


amount_of_bounding_glue_estimation_bp = Blueprint('amount_of_bounding_glue_estimation', __name__)
amount_of_bounding_glue_estimation_service = AmountOfBoundingGlueEstimationService()

@amount_of_bounding_glue_estimation_bp.route('/predict-amount-of-bounding-glue', methods=['POST'])
def predict_amount_of_bounding_glue():
    data = request.get_json()
    predictions = amount_of_bounding_glue_estimation_service.predict(data)
    return jsonify({'predictions': predictions})


task_manpower_estimation_bp = Blueprint('task_manpower_estimation', __name__)
task_manpower_estimation_service = TaskManpowerEstimationService()

@task_manpower_estimation_bp.route('/predict-task-manpower', methods=['POST'])
def predict_task_manpower():
    try:
        data = request.get_json()
        predictions = task_manpower_estimation_service.predict(data)
        return jsonify({'predictions': predictions})
    except Exception as e:
        return jsonify({'error': str(e)}), 500
    

task_skill_matching_bp = Blueprint('task_skill_matching', __name__)
task_skill_matching_service = TaskSkillMatchingService()

@task_skill_matching_bp.route('/predict-task-skill', methods=['POST'])
def predict_task_skill():
    try:
        # Get the JSON data from the request
        data = request.get_json()
        
        # Make predictions using the service
        predictions = task_skill_matching_service.predict(data)
        
        # Return the predictions as a JSON response
        return jsonify({'predictions': predictions})
    except Exception as e:
        return jsonify({'error': str(e)}), 500

oxygen_capacity_bp = Blueprint('oxygen_capacity_estimation', __name__)
oxygen_capacity_service = OxygenCapacityEstimationService()

@oxygen_capacity_bp.route('/predict-oxygen-capacity', methods=['POST'])
def predict_oxygen_capacity():
    data = request.get_json()
    predictions = oxygen_capacity_service.predict(data)
    return jsonify({'predictions': predictions})
