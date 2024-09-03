import logging
import os
import torch
from transformers import BertForSequenceClassification, BertTokenizer, DistilBertForSequenceClassification, DistilBertTokenizer, pipeline

# Set up logging to display detailed error messages
logging.basicConfig(level=logging.DEBUG)

class CommentClassificationService:
    def __init__(self):
        try:
            distilbert_dir = '/Users/seminipeiris/Desktop/Coral-Revive-REST-API/flask-service/app/models/comment-validation-service/distilbert_semantic_classifier'
            bert_dir = '/Users/seminipeiris/Desktop/Coral-Revive-REST-API/flask-service/app/models/comment-validation-service/coral_comment_classifier'
            bart_dir = '/Users/seminipeiris/Desktop/Coral-Revive-REST-API/flask-service/app/models/comment-validation-service/bart-large-mnli'

            if not os.path.exists(distilbert_dir):
                logging.error(f"DistilBERT directory not found: {distilbert_dir}")
            if not os.path.exists(bert_dir):
                logging.error(f"BERT directory not found: {bert_dir}")
            if not os.path.exists(bart_dir):
                logging.info(f"BART directory not found, downloading and saving to: {bart_dir}")
                os.makedirs(bart_dir, exist_ok=True)
                bart_classifier = pipeline("zero-shot-classification", model="facebook/bart-large-mnli")
                bart_classifier.save_pretrained(bart_dir)
                logging.info("BART model downloaded and saved successfully.")

            self.distilbert_model = DistilBertForSequenceClassification.from_pretrained(distilbert_dir, use_safetensors=True)
            self.distilbert_tokenizer = DistilBertTokenizer.from_pretrained(distilbert_dir)

            self.bert_model = BertForSequenceClassification.from_pretrained(bert_dir, use_safetensors=True)
            self.bert_tokenizer = BertTokenizer.from_pretrained(bert_dir)

            self.bart_classifier = pipeline("zero-shot-classification", model=bart_dir)
            logging.info("Models and tokenizers loaded successfully.")

            self.label_mapping = {1: 'True', 0: 'False', 2: 'semantic'}

        except Exception as e:
            logging.error("An error occurred during model loading.", exc_info=True)

    def classify_comment(self, post, comment, threshold=0.5):
        try:
            logging.info("Starting classification process.")
            
            # Step 1: Classify with DistilBERT to check if it's semantic
            logging.info("Tokenizing comment for DistilBERT.")
            distilbert_input = self.distilbert_tokenizer(comment, return_tensors='pt', truncation=True, padding=True, max_length=128)
            
            logging.info("Running DistilBERT model inference.")
            distilbert_output = self.distilbert_model(**distilbert_input)
            
            logging.info("Extracting logits from DistilBERT output.")
            distilbert_logits = distilbert_output.logits
            
            logging.info("Calculating softmax probabilities for DistilBERT output.")
            distilbert_probs = torch.nn.functional.softmax(distilbert_logits, dim=-1)
            
            logging.info(f"DistilBERT probabilities: {distilbert_probs.tolist()}")
            distilbert_pred_class = torch.argmax(distilbert_probs, dim=1).item()
            
            logging.info(f"DistilBERT predicted class: {distilbert_pred_class}")

            # Determine if DistilBERT predicts semantic
            if distilbert_pred_class == 1:
                logging.info("Comment classified as semantic by DistilBERT.")
                return "semantic"

            # Step 2: If DistilBERT does not predict semantic, use BERT to classify as true or false
            combined_input = f"Post: {post} Comment: {comment}"
            logging.info("Tokenizing combined input for BERT.")
            bert_inputs = self.bert_tokenizer(combined_input, return_tensors='pt', truncation=True, padding=True, max_length=128)
            
            logging.info("Running BERT model inference.")
            bert_outputs = self.bert_model(**bert_inputs)
            
            logging.info("Extracting logits from BERT output.")
            bert_logits = bert_outputs.logits
            
            logging.info("Calculating softmax probabilities for BERT output.")
            bert_probs = torch.nn.functional.softmax(bert_logits, dim=-1)
            
            logging.info(f"BERT probabilities: {bert_probs.tolist()}")
            bert_pred_class = torch.argmax(bert_probs, dim=1).item()
            
            logging.info(f"BERT predicted class: {bert_pred_class}")

            # Step 3: Use a threshold to classify uncertain comments as semantic
            max_prob = torch.max(bert_probs).item()
            logging.info(f"Maximum probability from BERT: {max_prob}")
            
            if max_prob < threshold:
                logging.info("Maximum probability is below threshold, classifying as semantic.")
                return "semantic"

            # Map BERT's prediction to label
            bert_prediction = self.label_mapping.get(bert_pred_class, "semantic")
            logging.info(f"BERT prediction mapped to label: {bert_prediction}")

            # Step 4: If BERT predicts factual (True/False), verify with BART
            if bert_prediction in ['True', 'False']:
                logging.info("Running BART zero-shot classification.")
                labels = ["true", "false"]
                bart_result = self.bart_classifier(comment, candidate_labels=labels, hypothesis_template="This comment is {}.")
                bart_prediction = bart_result['labels'][0]
                bart_score = bart_result['scores'][0]

                logging.info(f"BART prediction: {bart_prediction} with score: {bart_score}")

                # Decide final prediction based on BART's prediction
                final_prediction = "True" if bart_prediction == "true" else "False"
                logging.info(f"Final classification result after BART verification: {final_prediction}")
                return final_prediction

            return bert_prediction

        except Exception as e:
            logging.error("An error occurred during classification.", exc_info=True)
            return "error"



# Example usage for testing purposes
if __name__ == "__main__":
    service = CommentClassificationService()
    result = service.classify_comment("This is a sample post.", "This is a sample comment.")
    print("Classification result:", result)
    print(f"Current working directory: {os.getcwd()}")
