# TaskManagementOOP

## Overview
A simple, intuitive Java-based application designed to help users organize and track their daily tasks and rewards. Users can add tasks, complete them to earn points, and redeem these points for personal customized rewards.

## Prerequisites
Before you can run this application, you need to have Java installed on your system. If you do not have Java installed, follow these steps:

- **Download Java:**
  - Visit the [Oracle Java Downloads](https://www.oracle.com/java/technologies/downloads/#java11) page and download the Java Development Kit (JDK) for your operating system.
  - Follow the installation instructions on the website.

- **Set Java Environment Variable:**
  - Windows:
    - Search for Environment Variables in your system settings and add Java's `bin` directory to the Path variable.
  - macOS/Linux:
    - Add the following line to your shell configuration file (e.g., `.bashrc`, `.zshrc`):
      ```bash
      export PATH=$PATH:/path/to/java/bin
      ```
    - Replace `/path/to/java/bin` with the actual path to your JDK's `bin` directory.
    
## Setup
1. **Clone the repository:**
     ```bash
     git clone https://github.com/phoenix5980/TaskManagementOOP.git
     ```

2. **Navigate to the project directory:**
     ```bash
     cd TaskManagementOOP
     ```

3. **Compile the source code:**
     ```bash
     javac TaskManagementApp.java
     ```

4. **Run the application:**
     ```bash
     java TaskManagementApp
     ```

## Usage
- **Adding a Task:**
  - Click on the `+` button under the "To Do List" tab.
  - Fill in the task details including description, deadline, points and repetition.
  - If the task is repetitive, enable the repetition option and select the repetition frequency.
  - Click `OK` to save the task.

- **Completing a Task:**
  - Select the task you want to complete from the "To Do List".
  - Click the `Complete Task` button to mark it as completed and earn points.

- **Adding a Reward:**
  - Switch to the "Rewards" tab and click the `+` button.
  - Enter the reward details and the points needed for redemption.
  - If you want the reward to stay in the list for repetition, leave the availability as "Always Available", otherwise choose "One Time Only".
  - Click `OK` to save the reward.

- **Redeeming a Reward:**
  - Select a reward from the "Rewards" tab.
  - Click `Redeem Reward` if you have enough points for the reward you want.

- **Task History:**
  - You can always check your past task history by switching to the "Task History" tab.

- **Redeemed Rewards:**
  - You can always check your redeemed rewards in the second list under the "Reward" tab.

- **Deleting a Task/History/Reward:**
  - Select the task/history/reward you want to delete.
  - Click the `-` button to delete it.

## Data Persistence
The application saves tasks, task history, rewards, and reward history to text files upon closing. These files are loaded automatically when the application starts, ensuring that all data persists between sessions.

---
