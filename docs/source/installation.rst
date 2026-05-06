Installation
============

This page explains how to install and configure the TER Robotique project.

Prerequisites
-------------

Before starting, make sure the following tools are installed on your machine:

- Python 3.11 or newer
- Git
- VS Code
- pip

Clone the repository
--------------------

Clone the Git repository:

.. code-block:: bash

   git clone https://github.com/AlexMarchetto/TER---Projet-Robotique.git

Then move into the project directory:

.. code-block:: bash

   cd TER---Projet-Robotique

Create a virtual environment
----------------------------

Create a Python virtual environment:

.. code-block:: bash

   python -m venv venv

Activate the virtual environment.

On Windows:

.. code-block:: powershell

   .\venv\Scripts\activate

On Linux or macOS:

.. code-block:: bash

   source venv/bin/activate

Install dependencies
--------------------

Install the required Python packages:

.. code-block:: bash

   pip install -r requirements.txt

Build the documentation locally
-------------------------------

Move into the documentation directory:

.. code-block:: bash

   cd docs

Generate the HTML documentation:

.. code-block:: powershell

   .\make.bat html

The generated documentation will be available in:

.. code-block:: text

   docs/build/html/index.html

Run the project
---------------

Example command to launch the project:

.. code-block:: bash

   python main.py

Project structure
-----------------

.. code-block:: text

   TER---Projet-Robotique/
   ├── docs/
   │   ├── source/
   │   └── build/
   ├── src/
   ├── README.md
   └── requirements.txt

Troubleshooting
---------------

If the command ``make.bat`` is not recognized in PowerShell, use:

.. code-block:: powershell

   .\make.bat html

If dependencies fail to install, verify that Python and pip are correctly installed:

.. code-block:: bash

   python --version
   pip --version