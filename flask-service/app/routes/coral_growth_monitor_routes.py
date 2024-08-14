# app/routes/model_routes.py
from flask import Blueprint, request, jsonify
from app.services import CoralIdentificationService
import tempfile
import os

coral_growth_monitor = Blueprint('coral_growth_monitor', __name__)
model_service = CoralIdentificationService()


@coral_growth_monitor.route('/predict', methods=['POST'])
def predict():
    if 'image' not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files['image']

    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file:
        with tempfile.NamedTemporaryFile(delete=False) as temp_file:
            file.save(temp_file.name)
            filepath = temp_file.name

        prediction = model_service.predict(filepath)

        os.remove(filepath)

        return jsonify(prediction)
