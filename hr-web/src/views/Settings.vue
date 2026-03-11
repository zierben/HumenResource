<template>
  <div class="settings">
    <div class="page-header">
      <h2>系统设置</h2>
      <p class="sub-title">系统配置与参数管理</p>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover" class="setting-menu">
          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item index="budget">
              <el-icon><Coin /></el-icon>
              <span>预算规则</span>
            </el-menu-item>
            <el-menu-item index="pricing">
              <el-icon><PriceTag /></el-icon>
              <span>定价规则</span>
            </el-menu-item>
            <el-menu-item index="supplier">
              <el-icon><OfficeBuilding /></el-icon>
              <span>供应商规则</span>
            </el-menu-item>
            <el-menu-item index="approval">
              <el-icon><Finished /></el-icon>
              <span>审批流程</span>
            </el-menu-item>
            <el-menu-item index="zentao">
              <el-icon><Connection /></el-icon>
              <span>禅道对接</span>
            </el-menu-item>
            <el-menu-item index="system">
              <el-icon><Setting /></el-icon>
              <span>系统配置</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      
      <el-col :span="18">
        <el-card shadow="hover" v-show="activeMenu === 'budget'">
          <template #header>
            <div class="card-header">
              <span>预算规则配置</span>
            </div>
          </template>
          <el-form label-width="150px">
            <el-form-item label="预算前置校验">
              <el-switch v-model="settings.budgetCheck" />
              <div class="form-tip">开启后，所有外包需求必须先校验预算</div>
            </el-form-item>
            <el-form-item label="预算不足时">
              <el-radio-group v-model="settings.budgetAction">
                <el-radio label="block">阻止提交</el-radio>
                <el-radio label="warn">仅提示</el-radio>
              </el-radio-group>
            </el-form-item>
            <el-form-item label="超预算审批">
              <el-switch v-model="settings.overBudgetApproval" />
              <div class="form-tip">超预算需求需老板审批</div>
            </el-form-item>
            <el-form-item label="月度预算冻结">
              <el-switch v-model="settings.monthlyFreeze" />
              <div class="form-tip">预算执行率超100%时冻结新需求</div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card shadow="hover" v-show="activeMenu === 'pricing'">
          <template #header>
            <div class="card-header">
              <span>人员定价规则</span>
            </div>
          </template>
          <el-form label-width="150px">
            <el-form-item label="定价区间校验">
              <el-switch v-model="settings.priceCheck" />
            </el-form-item>
            <el-divider>岗位定价区间（元/人天）</el-divider>
            <el-form-item label="初级工程师">
              <el-input-number v-model="settings.juniorMin" :min="0" :precision="0" /> 
              <span class="mx-2">-</span>
              <el-input-number v-model="settings.juniorMax" :min="0" :precision="0" />
            </el-form-item>
            <el-form-item label="中级工程师">
              <el-input-number v-model="settings.midMin" :min="0" :precision="0" />
              <span class="mx-2">-</span>
              <el-input-number v-model="settings.midMax" :min="0" :precision="0" />
            </el-form-item>
            <el-form-item label="高级工程师">
              <el-input-number v-model="settings.seniorMin" :min="0" :precision="0" />
              <span class="mx-2">-</span>
              <el-input-number v-model="settings.seniorMax" :min="0" :precision="0" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card shadow="hover" v-show="activeMenu === 'supplier'">
          <template #header>
            <div class="card-header">
              <span>供应商管理规则</span>
            </div>
          </template>
          <el-form label-width="150px">
            <el-form-item label="供应商分级">
              <el-switch v-model="settings.supplierLevelEnabled" />
            </el-form-item>
            <el-form-item label="A级供应商条件">
              <div>交付率 ≥ <el-input-number v-model="settings.aLevelRate" :min="0" :max="100" :precision="0" size="small" /> %</div>
            </el-form-item>
            <el-form-item label="C级供应商限制">
              <div>交付率 < <el-input-number v-model="settings.cLevelRate" :min="0" :max="100" :precision="0" size="small" /> % 限制接单</div>
            </el-form-item>
            <el-form-item label="降级规则">
              <div>连续 <el-input-number v-model="settings.downgradeTimes" :min="1" :max="10" size="small" /> 次交付不达标自动降级</div>
            </el-form-item>
            <el-form-item>
              <el-button type="primary">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card shadow="hover" v-show="activeMenu === 'approval'">
          <template #header>
            <div class="card-header">
              <span>审批流程配置</span>
            </div>
          </template>
          <el-steps :active="3" align-center>
            <el-step title="项目经理" description="发起需求" />
            <el-step title="部门长" description="初审" />
            <el-step title="HR" description="合规审核" />
            <el-step title="财务" description="预算审核" />
            <el-step title="老板" description="终审（超预算）" />
          </el-steps>
          <el-divider />
          <el-form label-width="150px">
            <el-form-item label="审批时效提醒">
              <el-switch v-model="settings.approvalReminder" />
              <div class="form-tip">审批超时自动提醒</div>
            </el-form-item>
            <el-form-item label="超时时间">
              <el-input-number v-model="settings.approvalTimeout" :min="1" :max="72" size="small" /> 小时
            </el-form-item>
            <el-form-item>
              <el-button type="primary">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card shadow="hover" v-show="activeMenu === 'zentao'">
          <template #header>
            <div class="card-header">
              <span>禅道对接配置</span>
            </div>
          </template>
          <el-form label-width="150px">
            <el-form-item label="API地址">
              <el-input v-model="settings.zentaoUrl" placeholder="如：http://zentao.example.com" style="width: 300px" />
            </el-form-item>
            <el-form-item label="API密钥">
              <el-input v-model="settings.zentaoKey" type="password" placeholder="请输入API密钥" style="width: 300px" />
            </el-form-item>
            <el-form-item label="自动同步">
              <el-switch v-model="settings.zentaoSync" />
              <div class="form-tip">开启后自动同步工时、任务数据</div>
            </el-form-item>
            <el-form-item label="同步频率">
              <el-select v-model="settings.zentaoSyncFreq" style="width: 150px">
                <el-option label="实时" value="realtime" />
                <el-option label="每小时" value="hourly" />
                <el-option label="每天" value="daily" />
              </el-select>
            </el-form-item>
            <el-form-item>
              <el-button type="primary">保存配置</el-button>
              <el-button @click="testZentao">测试连接</el-button>
            </el-form-item>
          </el-form>
        </el-card>
        
        <el-card shadow="hover" v-show="activeMenu === 'system'">
          <template #header>
            <div class="card-header">
              <span>系统配置</span>
            </div>
          </template>
          <el-form label-width="150px">
            <el-form-item label="系统名称">
              <el-input v-model="settings.systemName" style="width: 200px" />
            </el-form-item>
            <el-form-item label="数据保留时间">
              <el-input-number v-model="settings.dataRetention" :min="1" :max="10" size="small" /> 年
            </el-form-item>
            <el-form-item label="启用日志审计">
              <el-switch v-model="settings.auditLog" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const activeMenu = ref('budget')

const settings = reactive({
  budgetCheck: true,
  budgetAction: 'block',
  overBudgetApproval: true,
  monthlyFreeze: true,
  priceCheck: true,
  juniorMin: 500,
  juniorMax: 800,
  midMin: 800,
  midMax: 1200,
  seniorMin: 1200,
  seniorMax: 2000,
  supplierLevelEnabled: true,
  aLevelRate: 90,
  cLevelRate: 60,
  downgradeTimes: 2,
  approvalReminder: true,
  approvalTimeout: 24,
  zentaoUrl: 'http://zentao.local',
  zentaoKey: '',
  zentaoSync: true,
  zentaoSyncFreq: 'daily',
  systemName: 'HR外包人力管理系统',
  dataRetention: 3,
  auditLog: true
})

const handleMenuSelect = (index) => {
  activeMenu.value = index
}

const testZentao = () => {
  ElMessage.success('禅道连接测试成功')
}
</script>

<style scoped>
.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  font-size: 20px;
}

.sub-title {
  margin: 5px 0 0;
  color: #909399;
  font-size: 14px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.setting-menu {
  height: fit-content;
}

.setting-menu .el-menu-item {
  height: 45px;
  line-height: 45px;
}

.form-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}

.mx-2 {
  margin: 0 8px;
}
</style>
