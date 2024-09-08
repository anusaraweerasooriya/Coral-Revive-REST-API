from flask import Blueprint, request, jsonify
from app.services.schedule_prioritization_service import SchedulePrioritizationService

schedule_routes = Blueprint('schedule_routes', 'schedule_routes')
schedule_prioritization_service = SchedulePrioritizationService()

@schedule_routes.route('/prioritize', methods=['POST'])
def prioritize_schedules():
    # Parse the incoming JSON data
    data = request.json
    schedules = data.get('schedules')
    
    # Check if schedules data is provided
    if not schedules:
        return jsonify({"error": "Schedules data is required."}), 400
    
    # Prioritize the schedules
    prioritized_schedules = schedule_prioritization_service.prioritize_schedules(schedules)
    
    # Convert numpy data types to native Python types using the method from the service
    prioritized_schedules = schedule_prioritization_service.convert_to_native_types(prioritized_schedules)
    
    # Return the prioritized schedules as a JSON response
    return jsonify(prioritized_schedules)
