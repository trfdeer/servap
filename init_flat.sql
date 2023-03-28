CREATE TABLE "sources" (
	"id"	INTEGER,
	"title"	TEXT NOT NULL UNIQUE,
	"url"	TEXT NOT NULL UNIQUE,
	"favicon_url"	TEXT,
	"folder"	TEXT,
	"date_added"	INTEGER NOT NULL,
	"date_updated"	INTEGER NOT NULL,
	PRIMARY KEY("id" AUTOINCREMENT)
);
