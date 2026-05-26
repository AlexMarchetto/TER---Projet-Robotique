# Configuration file for the Sphinx documentation builder.

# -- Project information -----------------------------------------------------

project = 'TER Projet Robotique'
copyright = '2026, Alex MARCHETTO & Alexis REBELO'
author = 'Alex MARCHETTO & Alexis REBELO'
release = '1.0'

# -- General configuration ---------------------------------------------------

extensions = []

templates_path = ['_templates']
exclude_patterns = []

# Locales (language management)
language = "en"

locale_dirs = ["locale/"]
gettext_compact = False

# -- Options for HTML output -------------------------------------------------

html_theme = 'sphinx_rtd_theme'
html_static_path = ['_static']

html_theme_options = {
    "collapse_navigation": False,
    "navigation_depth": 4,
    "titles_only": False,
}