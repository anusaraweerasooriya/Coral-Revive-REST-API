from flask import Blueprint, request, jsonify
from app.services.weather_prediction_service import WeatherPredictionService

weather_routes = Blueprint('weather_routes', __name__)
weather_service = WeatherPredictionService()

@weather_routes.route('/forecast_weather', methods=['POST'])
def forecast_weather():
    # Parse JSON request data
    data = request.json
    target_date = data.get('target_date')
    if not target_date:
        return jsonify({'error': 'Target date is required'}), 400
    
    # Get the prediction from the service
    prediction = weather_service.predict_future_weather(target_date)
    
    if prediction is None:
        return jsonify({'error': 'Prediction could not be made'}), 500
    
    return jsonify(prediction), 200
