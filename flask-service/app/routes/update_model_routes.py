from flask import Blueprint, jsonify
from app.services import initialize_services

services = initialize_services()
training_service = services["training_service"]

training_bp = Blueprint('training', __name__)

@training_bp.route('/train-model', methods=['POST'])
def trainmodel():
    try:
        training_service.train_model()
        return jsonify({"message": "Model training completed successfully."}), 200
    except Exception as e:
        print(f"Model training failed: {str(e)}")
        return jsonify({"message": f"Model training failed: {str(e)}"}), 500
