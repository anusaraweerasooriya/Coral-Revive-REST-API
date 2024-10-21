import logging
import os
import torch
from transformers import BertForSequenceClassification, BertTokenizer, DistilBertForSequenceClassification, DistilBertTokenizer, GPT2LMHeadModel, GPT2Tokenizer

# Set up logging to display detailed error messages
logging.basicConfig(level=logging.DEBUG)

class CommentClassificationService:
    def __init__(self):
        try:
            # Directly use the specified model paths
            distilbert_dir = "/app/models/comment-validation-service/distilbert_semantic_classifier"
            bert_dir = "/app/models/comment-validation-service/coral_comment_classifier"
            gpt2_dir = "/app/models/comment-validation-service/gpt2_model"

            # Check if directories exist, download models if necessary
            if not os.path.exists(distilbert_dir):
                logging.error(f"DistilBERT directory not found: {distilbert_dir}")
            if not os.path.exists(bert_dir):
                logging.error(f"BERT directory not found: {bert_dir}")
            if not os.path.exists(gpt2_dir):
                logging.info(f"GPT-2 directory not found, downloading and saving to: {gpt2_dir}")
                os.makedirs(gpt2_dir, exist_ok=True)
                gpt2_model = GPT2LMHeadModel.from_pretrained("gpt2")
                gpt2_model.save_pretrained(gpt2_dir)
                gpt2_tokenizer = GPT2Tokenizer.from_pretrained("gpt2")
                gpt2_tokenizer.save_pretrained(gpt2_dir)
                logging.info("GPT-2 model downloaded and saved successfully.")

            # Load models and tokenizers
            self.distilbert_model = DistilBertForSequenceClassification.from_pretrained(distilbert_dir, use_safetensors=True)
            self.distilbert_tokenizer = DistilBertTokenizer.from_pretrained(distilbert_dir)

            self.bert_model = BertForSequenceClassification.from_pretrained(bert_dir, use_safetensors=True)
            self.bert_tokenizer = BertTokenizer.from_pretrained(bert_dir)

            self.gpt2_model = GPT2LMHeadModel.from_pretrained(gpt2_dir).to(torch.device('cuda' if torch.cuda.is_available() else 'cpu'))
            self.gpt2_tokenizer = GPT2Tokenizer.from_pretrained(gpt2_dir)
            logging.info("Models and tokenizers loaded successfully.")

            self.label_mapping = {1: 'True', 0: 'False', 2: 'semantic'}

        except Exception as e:
            logging.error("An error occurred during model loading.", exc_info=True)

    def gpt2_score_text(self, text):
        try:
            inputs = self.gpt2_tokenizer.encode(text, return_tensors='pt').to(torch.device('cuda' if torch.cuda.is_available() else 'cpu'))
            with torch.no_grad():
                outputs = self.gpt2_model(inputs, labels=inputs)
                loss = outputs.loss
            return loss.item()  # Lower loss indicates more likely text
        except Exception as e:
            logging.error("An error occurred during GPT-2 scoring.", exc_info=True)
            return float('inf')  # Return a high score in case of an error

    def classify_comment(self, post, comment, threshold=0.5):
        try:
            logging.info("Starting classification process.")
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

            if distilbert_pred_class == 1:
                logging.info("Comment classified as semantic by DistilBERT.")
                return "semantic"

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

            max_prob = torch.max(bert_probs).item()
            logging.info(f"Maximum probability from BERT: {max_prob}")
            
            if max_prob < threshold:
                logging.info("Maximum probability is below threshold, classifying as semantic.")
                return "semantic"

            bert_prediction = self.label_mapping.get(bert_pred_class, "semantic")
            logging.info(f"BERT prediction mapped to label: {bert_prediction}")

            if bert_prediction in ['True', 'False']:
                logging.info("Running GPT-2 for verification.")
                true_text = f"The following statement is true: {comment}"
                false_text = f"The following statement is false: {comment}"

                true_score = self.gpt2_score_text(true_text)
                false_score = self.gpt2_score_text(false_text)

                logging.info(f"GPT-2 Scores - True: {true_score}, False: {false_score}")

                # Choose the label with the lower score (higher likelihood)
                final_prediction = "True" if true_score < false_score else "False"
                logging.info(f"Final classification result after GPT-2 verification: {final_prediction}")
                return final_prediction

            return bert_prediction

        except Exception as e:
            logging.error("An error occurred during classification.", exc_info=True)
            return "error"

if __name__ == "__main__":
    service = CommentClassificationService()
    result = service.classify_comment("This is a sample post.", "This is a sample comment.")
    print("Classification result:", result)
