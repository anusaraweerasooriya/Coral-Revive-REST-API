import joblib
import pandas as pd
import tensorflow as tf  
from sklearn.preprocessing import OneHotEncoder

class ReefBowlEstimationService:
    def __init__(self):
        # Load the saved model from the models directory
        model_path = 'app/models/resource_allocation_service/Reef_Bowl_Estimation_Model.pkl'
        self.model = joblib.load(model_path)

    def predict(self, input_data):
        # Convert the input data to a DataFrame
        df = pd.DataFrame(input_data)
        # Make predictions using the loaded model
        predictions = self.model.predict(df)
        return predictions.tolist()

class ManpowerEstimationService:
    def __init__(self):
        # Load the saved model from the models directory
        model_path = 'app/models/resource_allocation_service/Manpower_Required_Resource_Estimation_Model.h5'
        self.model = tf.keras.models.load_model(model_path, custom_objects={'mse': tf.keras.losses.MeanSquaredError()})
        
        # Initialize the OneHotEncoder for categorical features
        self.location_encoder = OneHotEncoder(handle_unknown='ignore', sparse=False)
        self.water_current_encoder = OneHotEncoder(handle_unknown='ignore', sparse=False)
        
        # Fit the encoders with the known categories (replace these with the actual categories from your training data)
        self.location_encoder.fit([['Trincomalee'], ['OtherLocation']])
        self.water_current_encoder.fit([['High'], ['Medium'], ['Low']])

    def preprocess(self, df):
        # One-hot encode the categorical variables
        location_encoded = self.location_encoder.transform(df[['Location']])
        water_current_encoded = self.water_current_encoder.transform(df[['Water_Current']])
        
        # Convert the encoded categorical data back into a DataFrame
        location_df = pd.DataFrame(location_encoded, columns=self.location_encoder.get_feature_names_out(['Location']))
        water_current_df = pd.DataFrame(water_current_encoded, columns=self.water_current_encoder.get_feature_names_out(['Water_Current']))
        
        # Drop original categorical columns and concatenate with the new one-hot encoded columns
        df = df.drop(columns=['Location', 'Water_Current'])
        df = pd.concat([df.reset_index(drop=True), location_df.reset_index(drop=True), water_current_df.reset_index(drop=True)], axis=1)
        
        return df

    def predict(self, input_data):
        # Convert the input data to a DataFrame
        df = pd.DataFrame(input_data)
        
        # Preprocess the data (handle categorical variables, etc.)
        df_processed = self.preprocess(df)
        
        # Make predictions using the loaded model
        predictions = self.model.predict(df_processed)
        return predictions.tolist()
    

class NumberOfBoatsEstimationService:
    def __init__(self):
        # Load the saved model from the models directory
        model_path = 'app/models/resource_allocation_service/Number_of_Boats_Resource_Estimation_Model.h5'
        self.model = tf.keras.models.load_model(model_path, custom_objects={'mse': tf.keras.losses.MeanSquaredError()})

    def preprocess(self, df):
        # Assume that the model expects two input features: Manpower_Required and Number_of_Reef_Bowls
        # Ensure that only these columns are passed to the model
        df_processed = df[['Manpower_Required', 'Number_of_Reef_Bowls']]
        
        # Convert any categorical data to numerical if needed (e.g., through encoding)
        # Example: If Manpower_Required is categorical, you would encode it here.
        
        return df_processed

    def predict(self, input_data):
        # Convert the input data to a DataFrame
        df = pd.DataFrame(input_data)
        
        # Preprocess the data (ensure only the necessary columns are passed)
        df_processed = self.preprocess(df)
        
        # Make predictions using the loaded model
        predictions = self.model.predict(df_processed)
        return predictions.tolist()


class NumberOfDivingKitsEstimationService:
    def __init__(self):
        # Load the saved model from the models directory
        model_path = 'app/models/resource_allocation_service/Number_of_Diving_Kits_Resource_Estimation_Model.h5'
        self.model = tf.keras.models.load_model(model_path, custom_objects={'mse': tf.keras.losses.MeanSquaredError()})

    def preprocess(self, df):
        # Ensure that only the necessary columns are passed to the model
        # Assuming the model expects only 'Manpower_Required' as input
        df_processed = df[['Manpower_Required']]
        
        # Convert any categorical data to numerical if needed (e.g., through encoding)
        
        return df_processed

    def predict(self, input_data):
        # Convert the input data to a DataFrame
        df = pd.DataFrame(input_data)
        
        # Preprocess the data (ensure only the necessary columns are passed)
        df_processed = self.preprocess(df)
        
        # Make predictions using the loaded model
        predictions = self.model.predict(df_processed)
        return predictions.tolist()
    

class NumberOfReefSegmentsEstimationService:
    def __init__(self):
        # Load the saved model from the models directory
        model_path = 'app/models/resource_allocation_service/Number_of_Reef_Segments_Resource_Estimation_Model.h5'
        self.model = tf.keras.models.load_model(model_path, custom_objects={'mse': tf.keras.losses.MeanSquaredError()})

        # Initialize OneHotEncoders for categorical features
        self.water_current_encoder = OneHotEncoder(handle_unknown='ignore', sparse=False)
        self.location_encoder = OneHotEncoder(handle_unknown='ignore', sparse=False)

        # Fit the encoders with known categories
        # These categories should be the same as those used during model training
        self.water_current_encoder.fit([['High'], ['Medium'], ['Low']])
        self.location_encoder.fit([['Trincomalee'], ['OtherLocation']])

    def preprocess(self, df):
        # One-hot encode the categorical variables
        water_current_encoded = self.water_current_encoder.transform(df[['Water_Current']])
        location_encoded = self.location_encoder.transform(df[['Location']])

        # Convert encoded categorical data back into DataFrames
        water_current_df = pd.DataFrame(water_current_encoded, columns=self.water_current_encoder.get_feature_names_out(['Water_Current']))
        location_df = pd.DataFrame(location_encoded, columns=self.location_encoder.get_feature_names_out(['Location']))

        # Drop original categorical columns and concatenate with the encoded ones
        df = df.drop(columns=['Water_Current', 'Location'])
        df = pd.concat([df.reset_index(drop=True), water_current_df.reset_index(drop=True), location_df.reset_index(drop=True)], axis=1)

        return df

    def predict(self, input_data):
        # Convert the input data to a DataFrame
        df = pd.DataFrame(input_data)

        # Preprocess the data (handle categorical variables, etc.)
        df_processed = self.preprocess(df)

        # Make predictions using the loaded model
        predictions = self.model.predict(df_processed)
        return predictions.tolist()
    
class AmountOfBoundingGlueEstimationService:
    def __init__(self):
        # Load the saved model from the models directory
        model_path = 'app/models/resource_allocation_service/Amount_of_Bounding_Glue_Resource_Estimation_Model.h5'
        self.model = tf.keras.models.load_model(model_path, custom_objects={'mse': tf.keras.losses.MeanSquaredError()})

    def preprocess(self, df):
        # Ensure that only the necessary columns are passed to the model
        # Assuming the model expects 'Number_of_Reef_Segments' as input
        df_processed = df[['Number_of_Reef_Segments']]
        
        return df_processed

    def predict(self, input_data):
        # Convert the input data to a DataFrame
        df = pd.DataFrame(input_data)
        
        # Preprocess the data (ensure only the necessary columns are passed)
        df_processed = self.preprocess(df)
        
        # Make predictions using the loaded model
        predictions = self.model.predict(df_processed)
        return predictions.tolist()
    

class TaskManpowerEstimationService:
    def __init__(self):
        # Load the saved model pipeline from the models directory
        model_path = 'app/models/resource_allocation_service/task_manpower_estimation.pkl'
        self.model = joblib.load(model_path)

    def predict(self, input_data):
        # Convert the input data to a DataFrame
        df = pd.DataFrame(input_data)
        
        # Make predictions using the loaded model
        predictions = self.model.predict(df)
        return predictions.tolist()
    
class TaskSkillMatchingService:
    def __init__(self):
        # Load the saved model from the models directory
        model_path = 'app/models/resource_allocation_service/Task_Skill_Matching_Model.joblib'
        self.model = joblib.load(model_path)

    def predict(self, input_data):
        # Convert the input data to a DataFrame
        df = pd.DataFrame(input_data)
        
        # Make predictions using the loaded model
        predictions = self.model.predict(df)
        return predictions.tolist()
    
class OxygenCapacityEstimationService:
    def __init__(self):
        # Load the saved model from the models directory
        model_path = 'app/models/resource_allocation_service/oxygen_capacity_estimation_model.joblib'
        self.model = joblib.load(model_path)

    def predict(self, input_data):
        # Convert the input data to a DataFrame
        df = pd.DataFrame(input_data)
        # Make predictions using the loaded model
        predictions = self.model.predict(df)
        return predictions.tolist()
