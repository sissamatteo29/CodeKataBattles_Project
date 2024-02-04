
CREATE SCHEMA sonarqube_schema;
CREATE USER sonarqube_user WITH PASSWORD 'sonarqube';
ALTER USER sonarqube_user SET search_path to sonarqube_schema;
GRANT ALL PRIVILEGES ON SCHEMA sonarqube_schema TO sonarqube_user;

