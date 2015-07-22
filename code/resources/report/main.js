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
  this.trace = $('.trace', e);
  this.chart = $('.chart', e);
  this.distribution = $('.distribution', e);

  $('.action-expand', e).click(this, function(e){
    e.data.expand();
  });

  $('.action-collapse', e).click(this, function(e){
    e.data.collapse();
  });
};

Exception.prototype.expand = function() {
  var i = this.index;
  var chart = this.chart[0];
  var distribution = this.distribution[0];

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

    var bar_data = [
        {'label': 'first', 'value':4, 'baseline':4.2, 'prediction': 2},
        {'label': 'second', 'value':2.1, 'baseline':3.1, 'prediction': 3},
        {'label': 'third', 'value':6.3, 'baseline':6.3, 'prediction': 4},
        {'label': 'fourth', 'value':5.7, 'baseline':3.2, 'prediction': 5},
        {'label': 'fifth', 'value':5, 'baseline':4.2, 'prediction': 3},
        {'label': 'sixth', 'value':4.2, 'baseline':10.2, 'prediction': 3},
        {'label': 'yet another', 'value':4.2, 'baseline':10.2, 'prediction': 3},
        {'label': 'and again', 'value':4.2, 'baseline':10.2, 'prediction': 3},
        {'label': 'and sss', 'value':4.2, 'baseline':10.2, 'prediction': 3}
    ];

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
};

/**********************************************************************************************************************/
var report = new Report();

$('#action-load-top20').click(function(){
  report.load(20);
});

$('#action-load-all').click(function(){
  report.load();
});

report.load(20);
