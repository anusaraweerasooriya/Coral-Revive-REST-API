import numpy as np
from datetime import datetime
import cv2


class CoralGrowthAnalysisService:
    def __init__(self):
        # Define growth stage thresholds for different species
        self.growth_stage_thresholds = {
            "Acropora": {
                "C": 5,
                "C+": 10,
                "A": 15,
                "A+": float("inf"),
            },
            "Acropora Genus": {
                "C": 3,
                "C+": 7,
                "A": 11,
                "A+": float("inf"),
            },
            "PocilloPora": {
                "C": 6,
                "C+": 10,
                "A": 20,
                "A+": float("inf"),
            }
        }

    def calculate_area(self, image_path, polyp_count, previous_area):

        image = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)

        blurred = cv2.GaussianBlur(image, (5, 5), 0)
        _, thresh = cv2.threshold(blurred, 100, 255, cv2.THRESH_BINARY_INV)

        contours, _ = cv2.findContours(
            thresh, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

        total_area = 0

        for contour in contours:
            contour_area = cv2.contourArea(contour)
            total_area += contour_area

        real_world_area = total_area * 0.1

        adjusted_area = real_world_area + (polyp_count * 0.05)

        return adjusted_area * 0.01

    def get_growth_stage(self, species, polyp_count):
        thresholds = self.growth_stage_thresholds.get(species, {})
        if polyp_count <= thresholds.get("C", float("inf")):
            return "C"
        elif polyp_count <= thresholds.get("C+", float("inf")):
            return "C+"
        elif polyp_count <= thresholds.get("A", float("inf")):
            return "A"
        else:
            return "A+"

    def calculate_growth_percentage(self, current_polyp_count, previous_polyp_count):
        if previous_polyp_count == 0:
            return 0
        return ((current_polyp_count - previous_polyp_count) / previous_polyp_count) * 100

    def calculate_growth_rate(self, current_polyp_count, previous_polyp_count, time_elapsed_days):
        if time_elapsed_days == 0:
            return 0
        return (current_polyp_count - previous_polyp_count) / time_elapsed_days

    def get_previous_growth_history(self, coral_id):
        previous_growth_history = {
            "polypCount": 0,
            "area": 0,
            "growthStage": "C",
            "timestamp": datetime.now()
        }
        return previous_growth_history

    def update_growth_history(self, coral_id, polyp_count, growth_stage, area, growth_percentage, growth_rate):
        growth_data = {
            "coralId": coral_id,
            "polypCount": polyp_count,
            "growthStage": growth_stage,
            "area": area,
            "growthPercentage": growth_percentage,
            "growthRate": growth_rate,
            "timestamp": datetime.now()
        }
        print(f"Updating database with: {growth_data}")

    def analyze_coral_growth(self, image_path, coral_id):

        from app.services.coral_identification_service import CoralIdentificationService
        from app.services.coral_polyp_count_service import CoralPolypCountService

        species_identification_service = CoralIdentificationService()
        species_prediction = species_identification_service.predict(image_path)

        species = species_prediction['predicted_label']

        polyp_count_service = CoralPolypCountService()
        polyp_count = polyp_count_service.predict_polyp_count(image_path)

        # Retrieve previous growth history first
        previous_history = self.get_previous_growth_history(coral_id)
        previous_area = previous_history.get("area", 0)

        # Now you can call calculate_area with all required arguments
        area = self.calculate_area(image_path, polyp_count, previous_area)

        polyp_count = polyp_count * 3
        previous_polyp_count = previous_history.get("polypCount", 0)
        previous_timestamp = previous_history.get("timestamp", datetime.now())

        growth_stage = self.get_growth_stage(species, polyp_count)

        growth_percentage = self.calculate_growth_percentage(
            polyp_count, previous_polyp_count)

        time_elapsed_days = (
            datetime.now() - previous_timestamp.replace(tzinfo=None)).days

        growth_rate = self.calculate_growth_rate(
            polyp_count, previous_polyp_count, time_elapsed_days)

        self.update_growth_history(
            coral_id, polyp_count, growth_stage, area, growth_percentage, growth_rate)

        return {
            "species": species,
            "polypCount": polyp_count,
            "growthStage": growth_stage,
            "growthPercentage": growth_percentage,
            "growthRate": growth_rate,
            "area": area
        }

    def analyze_growth_for_update(self, image_path, coral_id, previous_polyp_count, previous_area, previous_timestamp):

        from app.services.coral_identification_service import CoralIdentificationService
        from app.services.coral_polyp_count_service import CoralPolypCountService
        pre_weight = 3
        # Predict the species from the image
        species_identification_service = CoralIdentificationService()
        species_prediction = species_identification_service.predict(image_path)

        species = species_prediction['predicted_label']
        polyp_count_service = CoralPolypCountService()

        polyp_count = polyp_count_service.predict_polyp_count(image_path)
        polyp_count = polyp_count * pre_weight

        area = self.calculate_area(image_path, polyp_count, previous_area)
        growth_stage = self.get_growth_stage(species, polyp_count)
        growth_percentage = self.calculate_growth_percentage(
            polyp_count, previous_polyp_count)
        time_elapsed_days = (
            datetime.now() - previous_timestamp.replace(tzinfo=None)).days
        growth_rate = self.calculate_growth_rate(
            polyp_count, previous_polyp_count, time_elapsed_days)

        self.update_growth_history(
            coral_id, polyp_count, growth_stage, area, growth_percentage, growth_rate)

        return {
            "species": species,
            "polypCount": polyp_count,
            "growthStage": growth_stage,
            "growthPercentage": growth_percentage,
            "growthRate": growth_rate,
            "area": area
        }
