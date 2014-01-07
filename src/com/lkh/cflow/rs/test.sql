select orgid from cforguser where userid='U3306' and orgid in (select orgid from cforguser where userid='U3307');
