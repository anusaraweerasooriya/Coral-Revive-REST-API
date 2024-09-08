import numpy as np
from sklearn.preprocessing import normalize

class SchedulePrioritizationService:
    def __init__(self):
        # AHP matrix setup
        self.ahp_matrix = np.array([
            [1, 1/3, 5],    # urgency
            [3, 1, 7],      # impact
            [1/5, 1/7, 1]   # resource availability
        ])
        self.ahp_priorities = self.ahp_priority(self.ahp_matrix)

    def ahp_priority(self, matrix):
        column_sum = matrix.sum(axis=0)
        normalized_matrix = matrix / column_sum
        return normalized_matrix.mean(axis=1)

    def topsis_ranking(self, criteria_matrix, weights):
        normalized_matrix = normalize(criteria_matrix, axis=0, norm='l2')
        weighted_matrix = normalized_matrix * weights
        ideal_solution = weighted_matrix.max(axis=0)
        negative_ideal_solution = weighted_matrix.min(axis=0)
        dist_to_ideal = np.linalg.norm(weighted_matrix - ideal_solution, axis=1)
        dist_to_neg_ideal = np.linalg.norm(weighted_matrix - negative_ideal_solution, axis=1)
        closeness = dist_to_neg_ideal / (dist_to_ideal + dist_to_neg_ideal)
        return np.argsort(closeness)[::-1]

    def map_resource_availability(self, status):
        if status == "Scheduled":
            return 100
        elif status == "Pending_Resource_Availability":
            return 0
        else:
            return 50  # Default value if the status is something else

    def prioritize_schedules(self, schedules):
        # Explicit rule to prioritize scheduled tasks
        for schedule in schedules:
            schedule['resource_availability'] = self.map_resource_availability(schedule['status'])

        # Separate scheduled and pending tasks
        scheduled_tasks = [s for s in schedules if s['resource_availability'] == 100]
        pending_tasks = [s for s in schedules if s['resource_availability'] == 0]

        # Prioritize within each group using TOPSIS
        if scheduled_tasks:
            criteria_matrix_scheduled = np.array([[s['urgency'], s['impact'], s['resource_availability']] for s in scheduled_tasks])
            topsis_ranks_scheduled = self.topsis_ranking(criteria_matrix_scheduled, self.ahp_priorities)
            for i, rank in enumerate(topsis_ranks_scheduled):
                scheduled_tasks[i]['priorityRank'] = rank + 1

        if pending_tasks:
            criteria_matrix_pending = np.array([[s['urgency'], s['impact'], s['resource_availability']] for s in pending_tasks])
            topsis_ranks_pending = self.topsis_ranking(criteria_matrix_pending, self.ahp_priorities)
            for i, rank in enumerate(topsis_ranks_pending):
                pending_tasks[i]['priorityRank'] = len(scheduled_tasks) + rank + 1

        # Combine the results
        ranked_schedules = sorted(scheduled_tasks + pending_tasks, key=lambda x: x['priorityRank'])

        # Return only the required fields
        return [{"id": s['id'], "priorityRank": s['priorityRank']} for s in ranked_schedules]

    def convert_to_native_types(self, data):
        """
        Recursively convert numpy data types to native Python types
        """
        if isinstance(data, dict):
            return {key: self.convert_to_native_types(value) for key, value in data.items()}
        elif isinstance(data, list):
            return [self.convert_to_native_types(item) for item in data]
        elif isinstance(data, np.integer):
            return int(data)
        elif isinstance(data, np.floating):
            return float(data)
        elif isinstance(data, np.ndarray):
            return data.tolist()
        else:
            return data
