# battleweb

A frontend to [battlenet](https://github.com/winks/battlenet) written in [noir](http://webnoir.org).

Various tools acting as a frontend to the [Blizzard Community API](http://us.battle.net/wow/en/forum/2626217/).

## Usage

```bash
cp src/battleweb/settings.clj.dist src/battleweb/settings.clj
vi src/battleweb/settings.clj
mysql dbname < resources/misc/schema.sql
lein deps
lein run
```

## Dependencies

MySQL (preferred) or h2db.