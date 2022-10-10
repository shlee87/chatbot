#script for the lambda function

import spacy
import json
import pickle

def lambda_handler(event, context):
    with open('important2.pkl', 'rb') as f:
        b2 = pickle.load(f)
    nlp2=spacy.blank("en", vocab=b2.vocab)
    str = event['string']
    str = str.lower()
    doc = nlp2(str)
    final = []
    matches = b2(doc)
    for match_id, start, end in matches:
        string_id = b2.vocab.strings[match_id]
        span = doc[start:end]
        final.append(span.text)
    if len(final) == 0:
        containsSkills = False
    else:
        containsSkills = True
        
    x = {
        "skills" : final,
        "containsSkills" : containsSkills
    }
    
    return(x)
