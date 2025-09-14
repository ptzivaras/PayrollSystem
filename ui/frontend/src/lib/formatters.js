export const money = (value) =>
  new Intl.NumberFormat('en-US', { style: 'currency', currency: 'EUR' })
    .format(value ?? 0)
