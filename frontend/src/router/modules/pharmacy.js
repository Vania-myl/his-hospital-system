export default [
  { path: 'pharmacy/dispense', name: 'PharmacyDispense', component: () => import('@/views/pharmacy/Prescriptions.vue'), meta: { title: '药房发药', icon: 'el-icon-shopping-bag-1', roles: ['pharmacy'] } },
  { path: 'pharmacy/return', name: 'PharmacyReturn', component: () => import('@/views/pharmacy/Prescriptions.vue'), meta: { title: '药房退药', icon: 'el-icon-refresh-left', roles: ['pharmacy'] } },
  { path: 'pharmacy/drugs', name: 'PharmacyDrugs', component: () => import('@/views/pharmacy/Drugs.vue'), meta: { title: '药库管理', icon: 'el-icon-box', roles: ['pharmacy'] } },
  { path: 'pharmacy/transactions', name: 'PharmacyTransactions', component: () => import('@/views/pharmacy/Transactions.vue'), meta: { title: '药品交易记录', icon: 'el-icon-tickets', roles: ['pharmacy'] } }
]
