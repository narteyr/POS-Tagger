# Hidden Markov Model - Viterbi Algorithm

## Overview
This project implements a Hidden Markov Model (HMM) with the Viterbi Algorithm for part-of-speech (POS) tagging. The program reads training text and label files, computes transition and observation probabilities, and then applies the Viterbi algorithm to determine the most probable sequence of tags for a given sentence.

## Features
- Reads and processes training data to build transition and observation probability maps.
- Implements the Viterbi algorithm for POS tagging.
- Includes test cases for evaluating the model.
- Allows user input for real-time sentence tagging.

## How It Works
1. **Training Phase**: Reads training text and labels to build probability tables for transitions (state-to-state) and observations (state-to-word).
2. **Tagging Phase**: Uses the Viterbi algorithm to find the most likely sequence of tags for a given sentence.
3. **Console Testing**: Supports predefined test cases and interactive user input.

## File Structure
```
|-- texts/                        # Directory containing training and test datasets
|   |-- simple-train-sentences.txt
|   |-- simple-train-tags.txt
|   |-- brown-train-sentences.txt
|   |-- brown-train-tags.txt
|   |-- simple-test-sentences.txt
|   |-- simple-test-tags.txt
|   |-- brown-test-sentences.txt
|   |-- brown-test-tags.txt
|-- HMM.java                      # Main Java implementation of HMM and Viterbi algorithm
|-- README.md                      # Project documentation
```

## Installation & Usage
### Prerequisites
- Java (JDK 8 or later)

### Running the Program
1. **Compile the program**:
   ```sh
   javac HMM.java
   ```
2. **Run the program**:
   ```sh
   java HMM
   ```
3. **Test Cases & User Input**:
   - The program runs built-in test cases automatically.
   - Users can input sentences to get POS-tagged output interactively.

## Example Output
```
Enter a sentence: We should watch the dog work in a cave.
We/PRO should/MOD watch/V the/DET dog/N work/V in/P a/DET cave/N ./. 
```

## Authors
- **Richmond Nartey Tettey** (03/27/2025)
- **Samuel Jin** (03/27/2025)

## Future Improvements
- Improve handling of unknown words using smoothing techniques.
- Expand training datasets for better generalization.
- Optimize the Viterbi implementation for performance improvements.

## License
This project is released under the MIT License.
