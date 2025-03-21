$(document)
  .one('focus.textarea', '.auto-grow', function() {
    var savedValue = this.value
    this.value = ''
    this.baseScrollHeight = this.scrollHeight
    this.value = savedValue
  })
  .on('input.textarea', '.auto-grow', function() {
    var rows, minRows = this.getAttribute('data-min-rows') | 0;
    this.rows = minRows
    rows = Math.floor((this.scrollHeight - this.baseScrollHeight) / 16)
    this.rows = minRows + rows
  });