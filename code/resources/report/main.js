var Exception = function(index, e) {
  this.index = index;
  this.trace = $('.trace', e);
  this.chart = $('.chart', e);

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

  this.trace.show();
  this.chart.show();

  d3.json('report.json', function(json) {
    MG.data_graphic({
      data: MG.convert.date(json.data[i], 'date'),
      width: 800,
      height: 300,
      right: 100,
      target: chart,
      x_accessor: 'date',
      y_accessor: 'value'
    });
  });
};

Exception.prototype.collapse = function() {
  this.trace.hide();
  this.chart.hide();
};

d3.json('report.json', function(json) {
  $('#details').html(Mustache.render($('#tpl-details').html(), {errors: json.headers}));

  $('#details td.message').each(function(index, el){
    var exception = new Exception(index, el);
    exception.collapse();
  });
});