# app/routes/model_routes.py
from flask import Blueprint, request, jsonify
from app.services import CoralIdentificationService
from app.services import CoralGrowthAnalysisService
from datetime import datetime
import tempfile
import os

coral_growth_monitor = Blueprint('coral_growth_monitor', __name__)
model_service = CoralIdentificationService()
growth_analysis_service = CoralGrowthAnalysisService()


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


@coral_growth_monitor.route('/analyze-growth', methods=['POST'])
def analyze_growth():
    if 'image' not in request.files:
        return jsonify({"error": "No file part"}), 400

    file = request.files['image']

    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    if file:
        with tempfile.NamedTemporaryFile(delete=False) as temp_file:
            file.save(temp_file.name)
            filepath = temp_file.name

        coral_id = request.form.get('coralId', 'default_id')
        result = growth_analysis_service.analyze_coral_growth(
            filepath, coral_id)
        os.remove(filepath)

        return jsonify(result)


@coral_growth_monitor.route('/analyze-growth-for-update', methods=['POST'])
def analyze_growth_for_update():
    if 'image' not in request.files or 'coralId' not in request.form:
        return jsonify({"error": "Missing required fields"}), 400

    file = request.files['image']
    coral_id = request.form['coralId']

    if file.filename == '':
        return jsonify({"error": "No selected file"}), 400

    with tempfile.NamedTemporaryFile(delete=False) as temp_file:
        file.save(temp_file.name)
        image_path = temp_file.name

    previous_polyp_count = int(request.form.get('previousPolypCount', 0))
    previous_area = float(request.form.get('previousArea', 0))

    try:
        previous_timestamp_str = request.form.get(
            'previousTimestamp', datetime.now().isoformat())
        previous_timestamp = datetime.fromisoformat(previous_timestamp_str)
    except ValueError:
        return jsonify({"error": "Invalid timestamp format"}), 400

    result = growth_analysis_service.analyze_growth_for_update(
        image_path, coral_id, previous_polyp_count, previous_area, previous_timestamp
    )

    os.remove(image_path)

    return jsonify(result)
