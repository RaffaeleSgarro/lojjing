var Report = function() {};

Report.prototype.load = function(limit) {
  d3.json('report.json', function(json) {
    var errors = limit ? _.first(json.headers, limit) : json.headers;

    $('#details').html(Mustache.render($('#tpl-details').html(), {"errors": errors}));

    $('#details td.message').each(function(index, el){
      var exception = new Exception(index, el);
      exception.collapse();
    });
  });
}

var Exception = function(index, e) {
  this.index = index;
  this.toggler = $('.action-toggle', e);
  this.trace = $('.trace', e);
  this.chart = $('.chart', e);
  this.distribution = $('.distribution', e);

  $('.action-toggle', e).click(this, function(e){
    e.data.toggle();
  });
};

Exception.prototype.expand = function() {
  var i = this.index;
  var chart = this.chart[0];
  var distribution = this.distribution[0];

  this.collapsed = false;
  this.toggler.text('Chiudi');
  this.trace.show();
  this.chart.show();
  this.distribution.show();

  d3.json('report.json', function(json) {
    MG.data_graphic({
      data: MG.convert.date(json.data[i], 'date'),
      width: 800,
      height: 300,
      right: 100,
      target: chart,
      missing_is_zero: true,
      x_accessor: 'date',
      y_accessor: 'value'
    });

    MG.data_graphic({
        data: json.distributions[i],
        chart_type: 'bar',
        x_accessor: 'count',
        y_accessor: 'host',
        width: 800,
        height: 16 * json.distributions[i].length + 50,
        left: 150,
        animate_on_load: true,
        target: distribution
    });
  });
};

Exception.prototype.collapse = function() {
  this.trace.hide();
  this.chart.hide();
  this.distribution.hide();
  this.toggler.text('Apri');
  this.collapsed = true;
};

Exception.prototype.toggle = function() {
  if (this.collapsed) {
    this.expand();
  } else {
    this.collapse();
  }
}

/**********************************************************************************************************************/
var report = new Report();

$('#action-load-top20').click(function(){
  report.load(20);
});

$('#action-load-all').click(function(){
  report.load();
});

report.load(20);
