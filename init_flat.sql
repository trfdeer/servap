CREATE TABLE servap.sources (
	id INTEGER AUTO_INCREMENT,
	title VARCHAR(512) NOT NULL UNIQUE,
	url	VARCHAR(512) NOT NULL UNIQUE,
	favicon_url	TEXT,
	folder TEXT,
	date_added TIMESTAMP NOT NULL,
	date_updated TIMESTAMP NOT NULL,
	PRIMARY KEY(id)
);
