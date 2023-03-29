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

CREATE TABLE servap.links(
	id INTEGER AUTO_INCREMENT,
	source_id INTEGER NOT NULL,
	title VARCHAR(512) NOT NULL,
	link VARCHAR(2000) NOT NULL UNIQUE,
	description TEXT,
	publish_date TIMESTAMP NOT NULL,
	author VARCHAR(100),
	PRIMARY KEY (id),
	FOREIGN KEY (source_id) REFERENCES servap.sources(id)
);