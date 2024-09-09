import os
import numpy as np
import pandas as pd
from tensorflow.keras.models import load_model
import joblib

class WeatherPredictionService:
    def __init__(self):
        # Load the trained LSTM model
        model_path = os.path.join(os.getcwd(), 'app/models/scheduling-service/LSTM.keras')
        self.model = load_model(model_path)

        # Load the scaler used for normalizing the data
        scaler_path = os.path.join(os.getcwd(), 'app/models/scheduling-service/scaler.pkl')
        self.scaler = joblib.load(scaler_path)

        # Load the dataset
        data_file_path = os.path.join(os.getcwd(), 'app/datasets/scheduling-service/daily_average.csv')
        self.dataset = pd.read_csv(data_file_path)

        # Preprocess the data
        self.dataset['date'] = pd.to_datetime(self.dataset['date'])
        self.num_cols = ['temp', 'visibility', 'dew_point', 'pressure', 'humidity', 'wind_speed', 'wind_deg', 'rain_1h', 'clouds_all']
        self.dataset[self.num_cols] = self.scaler.transform(self.dataset[self.num_cols])

        # Add additional time features
        self.dataset['month'] = self.dataset['date'].dt.month
        self.dataset['day_of_month'] = self.dataset['date'].dt.day
        self.dataset['day_of_year'] = self.dataset['date'].dt.dayofyear
        self.dataset['day_of_week'] = self.dataset['date'].dt.dayofweek

        # Define the sequence length
        self.seq_length = 30

    def predict_future_weather(self, target_date):
        last_date = self.dataset['date'].max()
        
        target_date = pd.to_datetime(target_date)

        # Calculate how many days ahead the target date is from the last available date
        days_ahead = (target_date - last_date).days

        if days_ahead < 0:
            print("Target date is before the last available data")
            return None

        # Find the index of the last available data point
        last_index = self.dataset.index[self.dataset['date'] == last_date].tolist()[0]

        if last_index < self.seq_length - 1:
            print("Insufficient historical data to generate sequence")
            return None

        # Select the last `seq_length` data points as the initial input sequence
        input_seq = self.dataset[self.num_cols].values[last_index + 1 - self.seq_length: last_index + 1]
        input_seq = np.expand_dims(input_seq, axis=0)

        # Iterate for the number of days ahead, updating the sequence and predicting the next values
        for day in range(days_ahead):
            predicted_values = self.model.predict(input_seq, verbose=0)

            # Update the input sequence by appending the predicted values
            input_seq = np.concatenate((input_seq[:, 1:], np.expand_dims(predicted_values, axis=1)), axis=1)

        predicted_values = self.scaler.inverse_transform(predicted_values)[0]
        prediction_dict = dict(zip(self.num_cols, predicted_values))

        # Convert all values to Python-native types (specifically float)
        result = {
            "temp": float(prediction_dict.get('temp', 30)),
            "pressure": float(prediction_dict.get('pressure', 1012)),
            "humidity": float(prediction_dict.get('humidity', 86)),
            "windSpeed": float(prediction_dict.get('windSpeed', 40)),
            "rain1h": float(prediction_dict.get('rain1h', 0)),
            "cloudsAll": float(prediction_dict.get('cloudsAll', 40)),
            "weatherDescription": "Partly Cloudy"
        }
        
        return result